package io.sdmq.common.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

/**
 * Created by Xs.Tao on 2017/7/28.
 */
@ConfigurationProperties(prefix = RegistryProperties.SDMQ_REGISTRY_PREFIX)
public class RegistryProperties {

    public static final String SDMQ_REGISTRY_PREFIX = "sdmq.registry";


    private String enable = Objects.toString(Boolean.FALSE);
    private String serverList;

    private int maxRetries = 100;

    private int maxSleepTimeMilliseconds;

    private int baseSleepTimeMilliseconds;

    private String namespace = "io-sdmq";

    public String getServerList() {
        return serverList;
    }

    public void setServerList(String serverList) {
        this.serverList = serverList;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getMaxSleepTimeMilliseconds() {
        return maxSleepTimeMilliseconds;
    }

    public void setMaxSleepTimeMilliseconds(int maxSleepTimeMilliseconds) {
        this.maxSleepTimeMilliseconds = maxSleepTimeMilliseconds;
    }

    public int getBaseSleepTimeMilliseconds() {
        return baseSleepTimeMilliseconds;
    }

    public void setBaseSleepTimeMilliseconds(int baseSleepTimeMilliseconds) {
        this.baseSleepTimeMilliseconds = baseSleepTimeMilliseconds;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
