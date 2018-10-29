package io.sdmq.common.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import io.sdmq.queue.redis.RedisQueueImpl;

/**
 * Created by Xs.Tao on 2017/7/28.
 */
@Configuration
public class StartEventListener implements ApplicationListener<ContextRefreshedEvent> {

    public static final Logger LOGGER = LoggerFactory.getLogger(StartEventListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext ctx = event.getApplicationContext();
        if (ctx != null) {
            RedisQueueImpl redisQueue = ctx.getBean(RedisQueueImpl.class);
            String         regEnable  = AppEnvContext.getProperty("jikexiu.registry.enable", "false");
            if (!redisQueue.isRunning() && Boolean.parseBoolean(regEnable) == false) {
                LOGGER.info("starting Queue StandAlone Model ...");
                redisQueue.start();
            }
        }
    }


}
