package io.sdmq.queue;

import io.sdmq.queue.core.Job;
import io.sdmq.util.Status;

/**
 * Created by Xs.Tao on 2017/7/18.
 */
public class JobMsg implements java.io.Serializable, Job {

    private String topic;
    private String subtopic;
    /**
     * 预留字段
     **/
    private String id;
    private String bizKey;
    private long   delay;
    private long   ttl;
    /***预留字段**/
    private String body;
    /**
     * 扩展字段
     */
    private String extendData;
    private long   createTime = System.currentTimeMillis();
    private int    status     = Status.WaitPut.ordinal();

    @Override
    public String getExtendData() {
        return extendData;
    }

    public void setExtendData(String extendData) {
        this.extendData = extendData;
    }
    @Override
    public String getBizKey() {
        return bizKey;
    }

    public void setBizKey(String bizKey) {
        this.bizKey = bizKey;
    }
    @Override
    public String getTopic() {

        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    @Override
    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
    @Override
    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }
    @Override
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    @Override
    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    @Override
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    @Override
    public String getSubtopic() {
        return subtopic;
    }

    public void setSubtopic(String subtopic) {
        this.subtopic = subtopic;
    }
}

