package io.sdmq.common.conf;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import io.sdmq.queue.redis.RedisQueueImpl;

/**
 * Created by Xs.Tao on 2017/7/28.
 */
@Configuration
public class FailedEventListener implements ApplicationListener<ApplicationFailedEvent> {

    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        Throwable throwable = event.getException();
        handler(throwable, event);
    }

    private void handler(Throwable throwable, ApplicationFailedEvent event) {
        ApplicationContext ctx = event.getApplicationContext();
        if (ctx != null) {
            RedisQueueImpl redisQueue = ctx.getBean(RedisQueueImpl.class);
            if (redisQueue != null && redisQueue.isRunning()) {
                redisQueue.stop();
            }
        }
    }

}
