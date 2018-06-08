package io.sdmq.queue.redis.ready;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import io.sdmq.queue.JobMsg;
import io.sdmq.queue.core.ConsumeQueueProvider;
import io.sdmq.queue.core.Queue;
import io.sdmq.queue.redis.JobOperationService;
import io.sdmq.queue.redis.event.JobEventBus;
import io.sdmq.queue.redis.event.RedisJobTraceEvent;
import io.sdmq.queue.redis.support.DistributedLock;
import io.sdmq.queue.redis.support.RedisQueueProperties;
import io.sdmq.util.DateUtils;
import io.sdmq.util.NamedUtil;
import io.sdmq.util.Status;

/**
 * <pre>
 *     此客户端默认存在 并默认启动
 *
 *     发现数据 立即发MQ  后期后改善为push、pull 或者长连接之类的实现
 * </pre>
 * Created by Xs.Tao on 2017/7/19.
 */
public class RealTimeTask extends TimerTask {

    public static final Logger               LOGGER               = LoggerFactory.getLogger(RealTimeTask.class);
    private             RedisQueueProperties properties;
    private             JobOperationService  jobOperationService;
    private             Queue                delayQueue;
    private             DistributedLock      lock                 = null;
    private             ConsumeQueueProvider consumeQueueProvider = null;

    @Override
    public void run() {
        runTemplate();
    }

    private void runTemplate() {
        if (properties.isCluster()) {
            String lockName = NamedUtil.buildLockName(NamedUtil.buildRealTimeName(properties.getPrefix(), properties
                    .getName(), properties.getReadyName()));
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
                LOGGER.trace("开始轮询实时队列...%s");
            }
            //获取ready队列中的一个数据
            List<String> jobIds = jobOperationService.getReadyJob(10);
            if (jobIds != null && jobIds.size() > 0) {
                for (String jobId : jobIds) {
                    if (!StringUtils.isEmpty(jobId)) {
                        JobMsg j = jobOperationService.getJob(jobId);
                        if (j == null) {
                            this.jobOperationService.removeReadyJob(jobId);
                            LOGGER.warn("任务ID {} 元数据池没有数据", jobId);
                            continue;
                        }
                        if (j.getStatus() == Status.Delete.ordinal()) {
                            this.jobOperationService.removeJobToPool(jobId);
                            continue;
                        }
                        if (j.getStatus() != Status.Delete.ordinal()) {
                            if (!check(j)) {//没有达到执行时间 从新发送延时Buck中
                                j.setStatus(Status.Restore.ordinal());
                                delayQueue.push(j);
                                continue;
                            }
                            if (LOGGER.isInfoEnabled()) {
                                long runLong = j.getDelay() + j.getCreateTime();
                                String runDateString = DateUtils.format(new Date(runLong), DateUtils
                                        .FORMAT_YYYY_MM_DD_HH_MM_SS_SSS);
                                LOGGER.info(String.format("invokeTask %s target time : %s", jobId, runDateString));
                            }
                            consumeQueueProvider.consumer(j);
                            j.setStatus(Status.Finish.ordinal());
                            this.jobOperationService.updateJobStatus(jobId, Status.Finish);
                            this.jobOperationService.removeReadyJob(jobId);
                            this.jobOperationService.removeJobToPool(jobId);
                            JobEventBus.getInstance().post(new RedisJobTraceEvent(j));
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("处理实时队列发生错误", e);
        }
    }

    private boolean check(JobMsg job) {
        long runTime = job.getCreateTime() + job.getDelay(),
                currentTime = System.currentTimeMillis();
        return runTime <= currentTime;
    }


    public void setProperties(RedisQueueProperties properties) {
        this.properties = properties;
    }

    public void setJobOperationService(JobOperationService jobOperationService) {
        this.jobOperationService = jobOperationService;
    }

    /**
     * 注入队列操作对象 便于时间没有到触发条件下 执行重新发送
     */
    public void setDelayQueue(Queue delayQueue) {
        this.delayQueue = delayQueue;
    }

    public void setLock(DistributedLock lock) {
        this.lock = lock;
    }

    public void setConsumeQueueProvider(ConsumeQueueProvider consumeQueueProvider) {
        this.consumeQueueProvider = consumeQueueProvider;
    }
}
