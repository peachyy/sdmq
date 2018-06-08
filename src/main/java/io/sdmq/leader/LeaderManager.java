package io.sdmq.leader;

import io.sdmq.queue.redis.support.Lifecycle;

/**
 * Created by Xs.Tao on 2017/7/28.
 */
public interface LeaderManager extends Lifecycle {

    boolean isLeader();


}
