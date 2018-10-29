package io.sdmq.leader;

import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sdmq.queue.core.Queue;

/**
 * Created by Xs.Tao on 2017/7/28.
 */
public class LeaderWorkListener implements LeaderLatchListener {

    public static final Logger LOGGER = LoggerFactory.getLogger(LeaderWorkListener.class);
    private             Queue  queue;

    @Override
    public void isLeader() {
        LOGGER.warn("is starting work!");
        queue.start();
    }

    @Override
    public void notLeader() {
        LOGGER.warn("is stoping work!");
        queue.stop();
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }
}
