package io.sdmq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.sdmq.queue.redis.support.DistributedLock;
import io.sdmq.queue.redis.support.RedisDistributedLock;
import io.sdmq.queue.redis.support.RedisSupport;

/**
 * Created by Xs.Tao on 2017/7/20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestDelayQueue.class)
@WebAppConfiguration
public class DistributedLockTest {

    public static final Logger       LOGGER = LoggerFactory.getLogger(DistributedLockTest.class);
    @Autowired
    private             RedisSupport redisSupport;

    @Test
    public void test1() {
        final DistributedLock lock            = new RedisDistributedLock(redisSupport);
        ExecutorService       executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 20; i++) {
            final int index = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (index >= 10) {
                            lock.lock("test002");
                        } else {
                            lock.lock("test001");
                        }

                        LOGGER.info("我得到锁了 {} ", index);
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        if (index >= 10) {
                            lock.unlock("test002");
                        } else {
                            lock.unlock("test001");
                        }
                    }
                }
            });

        }
        try {
            Thread.sleep(1000000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
