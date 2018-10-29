package io.sdmq.common.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Map;

import io.sdmq.common.conf.AppEnvContext;
import io.sdmq.leader.LeaderManager;
import io.sdmq.leader.SimpleLeaderManager;
import io.sdmq.queue.redis.RedisQueueImpl;
import io.sdmq.queue.redis.support.RedisQueueProperties;

/**
 * Created by Xs.Tao on 2017/7/28.
 */
@Configuration
@Order(Ordered.LOWEST_PRECEDENCE + 1000)
public class HealthAutoConfiguration {


    @Autowired
    private HealthAggregator healthAggregator;


    @Bean
    @Autowired(required = false)
    @ConditionalOnMissingBean
    public HealthIndicator jikexiuHealthIndicator(RedisQueueImpl redisQueue,
                                                  RedisQueueProperties properties) {
        CompositeHealthIndicator compositeHealthIndicator = new
                CompositeHealthIndicator(healthAggregator);
        Map<String, LeaderManager> leaderManagerMap = AppEnvContext.getCtx().getBeansOfType(LeaderManager.class);
        LeaderManager              manager          = null;
        if (leaderManagerMap != null && !leaderManagerMap.isEmpty()) {
            manager = AppEnvContext.getCtx().getBean(SimpleLeaderManager.class);
        }

        compositeHealthIndicator.addHealthIndicator("dq", new QueueHealthIndicator(
                redisQueue, manager, properties));
        return compositeHealthIndicator;
    }


}
