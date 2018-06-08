package io.sdmq.common.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Xs.Tao on 2017/7/20.
 */
@ConfigurationProperties(prefix = RocketMQProperties.SDMQ_MQ_PREFIX)
public class RocketMQProperties {

    public static final String SDMQ_MQ_PREFIX   = "sdmq.reocketmq";
    private             String namesrvAddr;
    private             String filterSourceRoot = "/home/";

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public String getFilterSourceRoot() {
        return filterSourceRoot;
    }

    public void setFilterSourceRoot(String filterSourceRoot) {
        this.filterSourceRoot = filterSourceRoot;
    }
}
