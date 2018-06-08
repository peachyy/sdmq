package io.sdmq.queue.redis.support;

/**
 * Created by Xs.Tao on 2017/7/19.
 */
public interface Lifecycle {

    void start();

    void stop();

    boolean isRunning();
}
