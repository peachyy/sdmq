package io.sdmq.queue.redis.event;

import com.google.common.eventbus.Subscribe;

/**
 * Created by Xs.Tao on 2017/7/19.
 */
public interface JobEventListener {

    @Subscribe
    void listen(JobEvent event);


}
