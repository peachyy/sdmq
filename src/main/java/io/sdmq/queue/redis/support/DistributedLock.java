package io.sdmq.queue.redis.support;

/**
 * Created by Xs.Tao on 2017/7/20.
 */
public interface DistributedLock {

    boolean tryLock(String key);

    boolean tryLock(String key, long timeout);

    boolean lock(String key);

    void unlock(String key);
}
