package io.sdmq.common.autoconfigure;

import com.alibaba.druid.pool.DruidDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import io.sdmq.queue.redis.JobOperationService;
import io.sdmq.queue.redis.JobOperationServiceImpl;
import io.sdmq.queue.redis.RdbStore;
import io.sdmq.queue.redis.RedisQueueImpl;
import io.sdmq.queue.redis.bucket.BucketQueueManager;
import io.sdmq.queue.redis.event.JobEventBus;
import io.sdmq.queue.redis.event.JobEventListener;
import io.sdmq.queue.redis.event.RedisJobEventListener;
import io.sdmq.queue.redis.ready.ReadyQueueManager;
import io.sdmq.queue.redis.support.RedisDistributedLock;
import io.sdmq.queue.redis.support.RedisQueueProperties;
import io.sdmq.queue.redis.support.RedisSupport;
import redis.clients.jedis.Jedis;

/**
 * Created by Xs.Tao on 2017/7/19.
 */
@Configuration
@EnableConfigurationProperties(RedisQueueProperties.class)
@ConditionalOnClass(value = {Jedis.class, RedisQueueImpl.class})
public class RedisQueueAutoConfiguration {

    public static final Logger               LOGGER = LoggerFactory.getLogger(RedisQueueAutoConfiguration.class);
    @Autowired
    private             DruidConfig          druidConfig;
    @Autowired
    private             RedisQueueProperties properties;
    @Autowired
    private             StringRedisTemplate  template;

    private JobOperationService jobOperationService;


    @Bean
    public RedisSupport redisSupport() {
        RedisSupport support = new RedisSupport();
        support.setTemplate(template);
        return support;
    }

    /**
     * 分布式锁
     */
    @Bean
    @Autowired
    public RedisDistributedLock redisDistributedLock(RedisSupport redisSupport) {
        return new RedisDistributedLock(redisSupport);
    }

    @Bean
    @Autowired
    public JobOperationService JobOperationService(RedisSupport redisSupport) {
        JobOperationServiceImpl jobOperationService = new JobOperationServiceImpl();
        jobOperationService.setRedisSupport(redisSupport);
        jobOperationService.setProperties(properties);
        return jobOperationService;
    }

    @Bean
    @Autowired
    public BucketQueueManager BucketQueueManager(JobOperationService jobOperationService, RedisDistributedLock lock) {
        BucketQueueManager manager = new BucketQueueManager();
        manager.setProperties(properties);
        manager.setJobOperationService(jobOperationService);
        manager.setLock(lock);
        return manager;
    }

    @Bean
    public RdbStore rdbStore() {
        try {
            DruidDataSource ds = (DruidDataSource) druidConfig.newInstanceDruidDataSource();
            return new RdbStore(ds);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Bean
    @Autowired
    public JobEventListener jobEventListener(RdbStore store) {
        RedisJobEventListener eventListener = new RedisJobEventListener();
        eventListener.setStore(store);
        JobEventBus.getInstance().register(eventListener);
        return eventListener;
    }

    @Bean
    @Autowired
    public ReadyQueueManager readyQueueManager(JobOperationService jobOperationService, RedisDistributedLock lock) {
        ReadyQueueManager manager = new ReadyQueueManager();
        manager.setProperties(properties);
        manager.setJobOperationService(jobOperationService);
        manager.setLock(lock);
        return manager;
    }

    @Bean
    @Autowired
    public RedisQueueImpl redisQueueImpl(JobOperationService jobOperationService,
                                         BucketQueueManager bucketQueueManager, ReadyQueueManager readyQueueManager) {
        RedisQueueImpl redisQueue = new RedisQueueImpl();
        redisQueue.setProperties(properties);
        redisQueue.setJobOperationService(jobOperationService);
        redisQueue.setBucketQueueManager(bucketQueueManager);
        redisQueue.setReadyQueueManager(readyQueueManager);
        readyQueueManager.setDelayQueue(redisQueue);
        return redisQueue;
    }

}
