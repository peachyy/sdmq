package io.sdmq.queue.redis.event;

import io.sdmq.queue.JobMsg;

/**
 * Created by Xs.Tao on 2017/7/19.
 */
public interface JobEvent {

    JobMsg getJob();
}
