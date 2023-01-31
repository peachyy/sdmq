package io.sdmq.queue.core;

/**
 * Created by Xs.Tao on 2017/8/7.
 */
public interface Job {


    String getBizKey();


    String getTopic();


    String getId();


    long getDelay();


    long getTtl();


    String getBody();


    long getCreateTime();


    int getStatus();


    String getSubtopic();
    String getExtendData();

}
