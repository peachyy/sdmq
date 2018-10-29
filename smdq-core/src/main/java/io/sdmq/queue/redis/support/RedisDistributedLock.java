package io.sdmq.queue.redis.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sdmq.util.BlockUtils;

/**
 * Created by Xs.Tao on 2017/7/20.
 */
public class RedisDistributedLock implements DistributedLock {

    public static final  Logger       LOGGER               = LoggerFactory.getLogger(RedisDistributedLock.class);
    private static final long         DEFAULT_LOCK_TIMEOUT = 1000 * 60 * 3;
    private              RedisSupport redisSupport;

    public RedisDistributedLock() {

    }

    public RedisDistributedLock(RedisSupport redisSupport) {
        this.redisSupport = redisSupport;
    }

    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    @Override
    public boolean tryLock(String key) {
        throw new RuntimeException("待实现");
    }

    private boolean interLock(String key) {
        //得到锁后设置的过期时间，未得到锁返回0
        while (true) {
            long expireTime = getCuurentMillis() + DEFAULT_LOCK_TIMEOUT + 1;
            if (redisSupport.setNx(key, String.valueOf(expireTime))) {
                redisSupport.pExpire(key, DEFAULT_LOCK_TIMEOUT);
                return true;
            } else {
                String curLockTimeStr = redisSupport.get(key);
                if (!isEmpty(curLockTimeStr) &&
                        getCuurentMillis() > Long.valueOf(curLockTimeStr)) {
                    String setAftercurLockTimeStr = redisSupport.getSet(key, String.valueOf(expireTime));
                    //仍然过期,则得到锁
                    if (setAftercurLockTimeStr != null && setAftercurLockTimeStr.equals(curLockTimeStr)) {
                        redisSupport.pExpire(key, DEFAULT_LOCK_TIMEOUT);
                        return true;
                    }
                }
            }
            BlockUtils.sleep(10);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("没有获取锁 {} 正在等待...", key);
            }
        }
    }

    private long getCuurentMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public boolean tryLock(String key, long timeout) {
        throw new RuntimeException("待实现");
    }

    @Override
    public boolean lock(String key) {
        return interLock(key);
    }

    @Override
    public void unlock(String key) {
        if (!isEmpty(key)) {
            redisSupport.deleteKey(key);
        }
    }

    public void setRedisSupport(RedisSupport redisSupport) {
        this.redisSupport = redisSupport;
    }
}
