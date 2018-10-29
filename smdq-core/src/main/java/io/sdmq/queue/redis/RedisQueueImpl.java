package io.sdmq.queue.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.sdmq.exception.DelayQueueException;
import io.sdmq.exception.JobNotFoundException;
import io.sdmq.queue.JobMsg;
import io.sdmq.queue.core.Queue;
import io.sdmq.queue.redis.bucket.BucketQueueManager;
import io.sdmq.queue.redis.event.JobEventBus;
import io.sdmq.queue.redis.event.RedisJobTraceEvent;
import io.sdmq.queue.redis.ready.ReadyQueueManager;
import io.sdmq.queue.redis.support.RedisQueueProperties;
import io.sdmq.util.NamedUtil;
import io.sdmq.util.RdbOperation;
import io.sdmq.util.Status;

/**
 * Created by Xs.Tao on 2017/7/18.
 */
public class RedisQueueImpl implements Queue, java.io.Closeable {

    public static final Logger        LOGGER   = LoggerFactory.getLogger(RedisQueueImpl.class);
    private volatile    AtomicBoolean isRuning = new AtomicBoolean(false);
    private volatile    AtomicInteger pos      = new AtomicInteger(0);

    private JobOperationService jobOperationService;

    private RedisQueueProperties properties;

    private BucketQueueManager bucketQueueManager;

    private ReadyQueueManager readyQueueManager;


    public void push(JobMsg job) throws DelayQueueException {
        try {
            Assert.notNull(job, "Job不能为空");
            Assert.notNull(job.getId(), "JobId 不能为空");
            Assert.notNull(job.getDelay(), "Job Delay不能为空");
            if (job.getStatus() != Status.WaitPut.ordinal() && job.getStatus() != Status.Restore.ordinal()) {
                throw new IllegalArgumentException(String.format("任务%s状态异常", job.getId()));
            }
            String queueName = buildQueueName();
            if (job instanceof JobWrapp) {
                ((JobWrapp) job).setBuckedName(queueName);
            }
            this.jobOperationService.addJobToPool(job);
            JobEventBus.getInstance().post(new RedisJobTraceEvent(job, RdbOperation.INSERT));
            double score = Long.valueOf(job.getCreateTime() + job.getDelay());

            this.jobOperationService.addBucketJob(queueName, job.getId(), score);
            job.setStatus(Status.Delay.ordinal());
            this.jobOperationService.updateJobStatus(job.getId(), Status.Delay);
            JobEventBus.getInstance().post(new RedisJobTraceEvent(job));
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("task {} append success bucket to {} !", job.getId(), queueName);
            }
        } catch (Exception e) {
            LOGGER.error("添加任务失败", e);
            throw new DelayQueueException(e);
        }
    }


    @Override
    public boolean ack(String jobMsgId) throws DelayQueueException {
        throw new DelayQueueException("待实现");
    }

    @Override
    public long getSize() {
        throw new DelayQueueException("待实现");
    }

    @Override
    public void clear() {
        LOGGER.warn("正在执行清空队列操作 请注意");
        this.jobOperationService.clearAll();
    }

    @Override
    public boolean delete(String jobMsgId) {
        JobWrapp job = (JobWrapp) this.jobOperationService.getJob(jobMsgId);

        if (null == job) {
            return false;
        }
        if (job.getStatus() == Status.Finish.ordinal()) {
            throw new JobNotFoundException(String.format("任务 %s 已经完成", jobMsgId));
        }
        job.setStatus(Status.Delete.ordinal());
        this.jobOperationService.addJobToPool(job);//更新这个数据到池
        JobEventBus.getInstance().post(new RedisJobTraceEvent(job));
        //是否需要删除buck？
        if (!StringUtils.isEmpty(job.getBuckedName())) {
            this.jobOperationService.removeBucketKey(job.getBuckedName(), jobMsgId);
            this.jobOperationService.removeJobToPool(jobMsgId);//fix 删除源数据 这种放在源数据池中毫无意义
        }
        return true;
    }

    @Override
    public JobMsg getJob(String jobId) {
        Assert.notNull(jobId);
        JobMsg jobMsg = this.jobOperationService.getJob(jobId);
        return jobMsg;
    }

    @Override
    public String getImplementType() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void close() throws IOException {
        stop();
    }

    /////////////////////////help ////////////

    /**
     * 根据BuckSize轮询获取
     */
    public String buildQueueName() {
        return NamedUtil.buildBucketName(properties.getPrefix(), properties.getName(), getNextRoundRobin());
    }

    /**
     * 轮询算法  目前只适用于单机
     */
    private int getNextRoundRobin() {
        synchronized (this) {
            if (pos.get() >= properties.getBucketSize() || pos.get() < 0) {
                pos.set(1);
            } else {
                pos.getAndIncrement();
            }
        }
        return pos.get();
    }

    public void setJobOperationService(JobOperationService jobOperationService) {
        this.jobOperationService = jobOperationService;
    }

    public void setProperties(RedisQueueProperties properties) {
        this.properties = properties;
    }

    public void setBucketQueueManager(BucketQueueManager bucketQueueManager) {
        this.bucketQueueManager = bucketQueueManager;
    }

    public void setReadyQueueManager(ReadyQueueManager readyQueueManager) {
        this.readyQueueManager = readyQueueManager;
    }

    @Override
    public void start() {
        if (isRuning.compareAndSet(false, true)) {
            if (LOGGER.isInfoEnabled() && properties.isCluster()) {
                LOGGER.info("Cluster Model Starting...");
            }
            bucketQueueManager.start();
            readyQueueManager.start();
        }
    }

    @Override
    public void stop() {
        if (isRuning.compareAndSet(true, false)) {
            bucketQueueManager.stop();
            readyQueueManager.stop();
        }

    }

    @Override
    public boolean isRunning() {
        return isRuning.get();
    }
}
