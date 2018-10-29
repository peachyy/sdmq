package io.sdmq.common.autoconfigure;

import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import io.sdmq.common.conf.AppEnvContext;
import io.sdmq.leader.LeaderManager;
import io.sdmq.leader.LeaderWorkListener;
import io.sdmq.leader.ServerNode;
import io.sdmq.leader.SimpleLeaderManager;
import io.sdmq.queue.redis.RedisQueueImpl;
import io.sdmq.util.IpUtils;

/**
 * Created by Xs.Tao on 2017/7/28.
 */
@Configuration
@EnableConfigurationProperties(RegistryProperties.class)
@ConditionalOnProperty(prefix = RegistryProperties.SDMQ_REGISTRY_PREFIX, value = "enable", havingValue = "true")
@ConditionalOnClass(value = {ZooKeeperServer.class, CuratorFrameworkFactory.class})
@Order(Ordered.LOWEST_PRECEDENCE + 50)
public class LeaderAutoConfiguration {

    @Autowired
    private RegistryProperties registryProperties;


    @Bean
    @Autowired
    @ConditionalOnMissingBean
    public LeaderLatchListener leaderLatchListenerImpl(RedisQueueImpl redisQueueImpl) {
        LeaderWorkListener listener = new LeaderWorkListener();
        listener.setQueue(redisQueueImpl);
        return listener;
    }

    @Bean(name = "simpleLeaderManager", initMethod = "init", destroyMethod = "stop")
    @Autowired
    @ConditionalOnMissingBean
    public LeaderManager leaderManager(LeaderLatchListener leaderLatchListener) {
        SimpleLeaderManager slm = new SimpleLeaderManager();
        slm.setProperties(registryProperties);
        slm.addListener(leaderLatchListener);
        ServerNode.NAMESPACE = registryProperties.getNamespace();
        slm.setServerName(IpUtils.getIp() + ":" + AppEnvContext.getProperty("server.port"));
        return slm;
    }
}
