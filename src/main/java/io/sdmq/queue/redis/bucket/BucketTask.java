package io.sdmq.queue.redis.bucket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.TimerTask;

import io.sdmq.queue.JobMsg;
import io.sdmq.queue.redis.JobOperationService;
import io.sdmq.queue.redis.event.JobEventBus;
import io.sdmq.queue.redis.event.RedisJobTraceEvent;
import io.sdmq.queue.redis.support.DistributedLock;
import io.sdmq.queue.redis.support.RedisQueueProperties;
import io.sdmq.util.NamedUtil;
import io.sdmq.util.Status;

/**
 * Created by Xs.Tao on 2017/7/18.
 */
public class BucketTask extends TimerTask {

    public static final Logger               LOGGER   = LoggerFactory.getLogger(BucketTask.class);
    public static final long                 TIME_OUT = 1000 * 30;
    private             String               bucketName;
    private             String               poolName;
    private             String               readyName;
    private             JobOperationService  jobOperationService;
    private             DistributedLock      lock     = null;
    private             RedisQueueProperties properties;


    protected BucketTask(String bucketName) {
        this.bucketName = bucketName;
    }

    @Override
    public void run() {
        runTemplate();
    }

    private void runTemplate() {
        if (properties.isCluster()) {
            String lockName = NamedUtil.buildLockName(bucketName);
            try {
                lock.lock(lockName);
                runInstance();
            } finally {
                lock.unlock(lockName);
            }
        } else {
            runInstance();
        }
    }

    private void runInstance() {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(String.format("开始轮询...%s", bucketName));
            }
            List<String> jobs = peeks();
            if (jobs != null && jobs.size() > 0) {
                for (String jobId : jobs) {
                    if (!StringUtils.isEmpty(jobId)) {
                        JobMsg job = jobOperationService.getJob(jobId);
                        if (null == job) {//如果数据为null
                            jobOperationService.removeBucketKey(bucketName, jobId);
                            continue;
                        }
                        if (job.getStatus() == Status.Delete.ordinal()) {
                            this.jobOperationService.removeJobToPool(jobId);
                            this.jobOperationService.removeBucketKey(bucketName, jobId);
                            continue;
                        }
                        //不是延迟状态的数据 不处理 以防数据被消费了 状态还没有更新的问题
                        if (job.getStatus() != Status.Delay.ordinal()) {
                            continue;
                        }
                        long delay = job.getCreateTime() + job.getDelay() - System.currentTimeMillis();
                        if (delay <= 0) {//到了当前时间了 把数据从buck移除 并添加到实时队列
                            job.setStatus(Status.Ready.ordinal());
                            //首先执行修改元数据状态 避免timer竞争到数据了 而状态不一致而出现问题。
                            JobEventBus.getInstance().post(new RedisJobTraceEvent(job));
                            this.jobOperationService.updateJobStatus(job.getId(), Status.Ready);
                            jobOperationService.addReadyTime(readyName, jobId);
                            jobOperationService.removeBucketKey(bucketName, jobId);//删除
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug(String.format("jobId %s 达到了运行时间了", jobId));
                            }
                        } else {//还没有到当前时间
                            //SKIP
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(String.format("处理延时队 %s 列发生错误", bucketName), e);
        }
    }

    private String peek() {
        try {
            String jobMsgId = jobOperationService.getBucketTop1Job(bucketName);
            return jobMsgId;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 批量偷取 提高实时性
     */
    private List<String> peeks() {
        try {
            List<String> lst = jobOperationService.getBucketTopJobs(bucketName, 10);
            return lst;
        } catch (Exception e) {
            throw e;
        }
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public String getReadyName() {
        return readyName;
    }

    public void setReadyName(String readyName) {
        this.readyName = readyName;
    }

    public void setJobOperationService(JobOperationService jobOperationService) {
        this.jobOperationService = jobOperationService;
    }

    public void setLock(DistributedLock lock) {
        this.lock = lock;
    }

    public void setProperties(RedisQueueProperties properties) {
        this.properties = properties;
    }
}
