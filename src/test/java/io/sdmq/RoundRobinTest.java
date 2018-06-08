package io.sdmq;

import io.sdmq.queue.redis.RedisQueueImpl;
import io.sdmq.queue.redis.support.RedisQueueProperties;

/**
 * Created by Xs.Tao on 2017/7/19.
 */
public class RoundRobinTest {

    public static void main(String[] args) {
        RedisQueueProperties properties = new RedisQueueProperties();
        properties.setBucketSize(1);
        properties.setPrefix("com.tmk");
        properties.setName("b");
        final RedisQueueImpl redisQueue = new RedisQueueImpl();
        redisQueue.setProperties(properties);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    System.out.println(redisQueue.buildQueueName());
                    System.out.println(redisQueue.buildQueueName());
                    System.out.println(redisQueue.buildQueueName());
                    System.out.println(redisQueue.buildQueueName());
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    System.out.println(redisQueue.buildQueueName());
                    System.out.println(redisQueue.buildQueueName());
                    System.out.println(redisQueue.buildQueueName());
                    System.out.println(redisQueue.buildQueueName());
                    System.out.println(redisQueue.buildQueueName());
                    System.out.println(redisQueue.buildQueueName());
                    System.out.println(redisQueue.buildQueueName());
                    System.out.println(redisQueue.buildQueueName());
                }
            }
        }).start();
    }
}
