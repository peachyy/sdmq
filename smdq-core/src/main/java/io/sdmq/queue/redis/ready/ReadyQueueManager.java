package io.sdmq.queue.redis.ready;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;

import io.sdmq.common.extension.ExtensionLoader;
import io.sdmq.queue.core.ConsumeQueueProvider;
import io.sdmq.queue.core.Queue;
import io.sdmq.queue.redis.JobOperationService;
import io.sdmq.queue.redis.support.DistributedLock;
import io.sdmq.queue.redis.support.Lifecycle;
import io.sdmq.queue.redis.support.RedisQueueProperties;

/**
 * Created by Xs.Tao on 2017/7/19.
 */
public class ReadyQueueManager implements Lifecycle {

    public static final Logger               LOGGER      = LoggerFactory.getLogger(ReadyQueueManager.class);
    public static final String               THREAD_NAME = "sdmq-ready-queue-%s";
    public              boolean              daemon      = true;
    private volatile    AtomicBoolean        isRuning    = new AtomicBoolean(false);
    private             RedisQueueProperties properties;
    private             Timer                timer;
    private             JobOperationService  jobOperationService;
    private             Queue                delayQueue;
    private             String               threadName;
    private             DistributedLock      lock        = null;


    @Override
    public void start() {
        if (isRuning.compareAndSet(false, true)) {
            threadName = String.format(THREAD_NAME, 1);
            timer = new Timer(threadName, daemon);
            RealTimeTask task = new RealTimeTask();
            task.setProperties(properties);
            task.setJobOperationService(jobOperationService);
            task.setDelayQueue(delayQueue);
            task.setLock(lock);
            task.setConsumeQueueProvider(ExtensionLoader.getExtension(ConsumeQueueProvider.class));
            timer.schedule(task, 500, properties.getReadyRoundRobinTime());
            LOGGER.info(String.format("Starting Ready Thead %s ....", threadName));
        }
    }

    @Override
    public void stop() {
        if (isRuning.compareAndSet(true, false)) {
            if (timer != null) {
                timer.cancel();
                LOGGER.info(String.format("stoping timer %s .....", threadName));
            }
        }
    }

    @Override
    public boolean isRunning() {
        return isRuning.get();
    }

    public void setProperties(RedisQueueProperties properties) {
        this.properties = properties;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public void setDelayQueue(Queue delayQueue) {
        this.delayQueue = delayQueue;
    }

    public void setJobOperationService(JobOperationService jobOperationService) {
        this.jobOperationService = jobOperationService;
    }

    public void setLock(DistributedLock lock) {
        this.lock = lock;
    }
}
