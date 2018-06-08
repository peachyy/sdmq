package io.sdmq.common.autoconfigure;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import io.sdmq.leader.LeaderManager;
import io.sdmq.leader.ServerNode;
import io.sdmq.queue.core.Queue;
import io.sdmq.queue.redis.RedisQueueImpl;
import io.sdmq.queue.redis.support.RedisQueueProperties;

public class QueueHealthIndicator implements HealthIndicator {


    private Queue                queue;
    private LeaderManager        leaderManager;
    private RedisQueueProperties redisQueueProperties;

    public QueueHealthIndicator(RedisQueueImpl queue, LeaderManager leaderManager, RedisQueueProperties
            redisQueueProperties) {
        this.queue = queue;
        this.leaderManager = leaderManager;
        this.redisQueueProperties = redisQueueProperties;
    }

    @Override
    public Health health() {
        try {
            Health.Builder builder = Health.up();
            if (leaderManager == null) {
                builder.withDetail("run", queue.isRunning());
            } else {
                builder.withDetail("run", queue.isRunning()).withDetail("isMaster", leaderManager.isLeader());
            }
            return builder
                    .withDetail("isCluster", redisQueueProperties.isCluster())
                    .withDetail("bucketSize", redisQueueProperties.getBucketSize())
                    .withDetail("prefix", redisQueueProperties.getPrefix())
                    .withDetail("namespace", ServerNode.NAMESPACE)
                    .build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
