package io.sdmq.queue.redis.support;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Xs.Tao on 2017/7/18.
 */
@ConfigurationProperties(prefix = RedisQueueProperties.REDIS_QUEUE_PREFIX)
public class RedisQueueProperties {

    public static final String REDIS_QUEUE_PREFIX = "sdmq.rqueue";
    private             String name;
    private             String prefix             = "io.sdmq";
    private             String originPool         = "pools";
    private             String readyName          = "ready";
    private             int    bucketSize         = 3;

    /**
     * buck轮询时间
     **/
    private long    buckRoundRobinTime  = 300;
    /**
     * ready轮询时间
     **/
    private long    readyRoundRobinTime = 200;
    private boolean cluster             = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getOriginPool() {
        return originPool;
    }

    public void setOriginPool(String originPool) {
        this.originPool = originPool;
    }

    public String getReadyName() {
        return readyName;
    }

    public void setReadyName(String readyName) {
        this.readyName = readyName;
    }

    public int getBucketSize() {
        return bucketSize;
    }

    public void setBucketSize(int bucketSize) {
        this.bucketSize = bucketSize;
    }

    public boolean isCluster() {
        return cluster;
    }

    public void setCluster(boolean cluster) {
        this.cluster = cluster;
    }

    public long getBuckRoundRobinTime() {
        if (buckRoundRobinTime <= 0) {
            buckRoundRobinTime = 500;
        }
        return buckRoundRobinTime;
    }

    public void setBuckRoundRobinTime(long buckRoundRobinTime) {
        this.buckRoundRobinTime = buckRoundRobinTime;
    }

    public long getReadyRoundRobinTime() {
        if (readyRoundRobinTime <= 0) {
            readyRoundRobinTime = 500;
        }
        return readyRoundRobinTime;
    }

    public void setReadyRoundRobinTime(long readyRoundRobinTime) {
        this.readyRoundRobinTime = readyRoundRobinTime;
    }
}
