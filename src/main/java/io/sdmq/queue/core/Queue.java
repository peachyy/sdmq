package io.sdmq.queue.core;

import io.sdmq.exception.DelayQueueException;
import io.sdmq.queue.JobMsg;
import io.sdmq.queue.redis.support.Lifecycle;

/**
 * Created by Xs.Tao on 2017/7/18.
 */
public interface Queue extends Lifecycle {

    void push(JobMsg job) throws DelayQueueException;


    boolean ack(String jobMsgId) throws DelayQueueException;

    long getSize();

    void clear();

    boolean delete(String jobMsgId);

    JobMsg getJob(String jobId);

    String getImplementType();


}
