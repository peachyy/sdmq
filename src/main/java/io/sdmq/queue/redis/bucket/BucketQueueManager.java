package io.sdmq.queue.redis.bucket;

import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;

import io.sdmq.queue.redis.JobOperationService;
import io.sdmq.queue.redis.support.DistributedLock;
import io.sdmq.queue.redis.support.Lifecycle;
import io.sdmq.queue.redis.support.RedisQueueProperties;
import io.sdmq.util.NamedUtil;

/**
 * Created by Xs.Tao on 2017/7/18.
 */
public class BucketQueueManager implements Lifecycle {

    public static final Logger               LOGGER      = LoggerFactory.getLogger(BucketQueueManager.class);
    public static final String               THREAD_NAME = "sdmq-delay-queue-%s";
    public              boolean              daemon      = true;
    private volatile    AtomicBoolean        isRuning    = new AtomicBoolean(false);
    private             RedisQueueProperties properties;
    private             Map<String, Timer>   HOLD_TIMES  = Maps.newConcurrentMap();
    private             JobOperationService  jobOperationService;
    private             DistributedLock      lock        = null;

    private int checkBucketNum(int bucketSize) {
        if (bucketSize <= 0) {
            bucketSize = 1;
        }
        return bucketSize;
    }

//    public static void main(String[] args) {
//        BucketQueueManager manager=new BucketQueueManager();
//        RedisQueueProperties redisQueueProperties=new RedisQueueProperties();
//        manager.setProperties(redisQueueProperties);
//       manager.start();
//        try {
//            Thread.sleep(10000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println(manager.isRunning());
//        manager.stop();
//        System.out.println(manager.isRunning());
//    }


    @Override
    public void start() {
        int bucketSize = checkBucketNum(properties.getBucketSize());
        if (isRuning.compareAndSet(false, true)) {
            for (int i = 1; i <= bucketSize; i++) {
                String     bName = NamedUtil.buildBucketName(properties.getPrefix(), properties.getName(), i);
                BucketTask task  = new BucketTask(bName);
                task.setJobOperationService(jobOperationService);
                task.setPoolName(NamedUtil.buildPoolName(properties.getPrefix(), properties.getName(), properties
                        .getOriginPool()));
                task.setReadyName(NamedUtil.buildPoolName(properties.getPrefix(), properties.getName(), properties
                        .getReadyName()));
                task.setProperties(properties);
                task.setLock(lock);
                String threadName = String.format(THREAD_NAME, i);
                Timer  timer      = new Timer(threadName, daemon);
                timer.schedule(task, 500, properties.getBuckRoundRobinTime());
                HOLD_TIMES.put(threadName, timer);
                LOGGER.info(String.format("Starting Bucket Thead %s ....", threadName));
            }

        }
    }

    @Override
    public void stop() {
        if (isRuning.compareAndSet(true, false)) {
            if (HOLD_TIMES != null && HOLD_TIMES.size() > 0) {
                for (Map.Entry<String, Timer> entry : HOLD_TIMES.entrySet()) {
                    String n     = entry.getKey();
                    Timer  timer = entry.getValue();
                    timer.cancel();
                    LOGGER.info(String.format("stoping timer %s .....", n));
                }
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

    public void setJobOperationService(JobOperationService jobOperationService) {
        this.jobOperationService = jobOperationService;
    }

    public void setLock(DistributedLock lock) {
        this.lock = lock;
    }
}
