package io.sdmq.queue.redis.event;

import io.sdmq.queue.JobMsg;
import io.sdmq.queue.redis.RdbStore;
import io.sdmq.util.RdbOperation;
import io.sdmq.util.Status;

/**
 * Created by Xs.Tao on 2017/7/19.
 */
public class RedisJobEventListener implements JobEventListener {

    private RdbStore store;

    @Override
    public void listen(JobEvent event) {
        if (event instanceof RedisJobTraceEvent) {
            RedisJobTraceEvent e   = (RedisJobTraceEvent) event;
            JobMsg             job = e.getJob();
            if (e.getOperation() == RdbOperation.INSERT && job.getStatus() != Status.Restore.ordinal()) {
                store.insertJob(job);
            } else {
                store.updateJobsStatus(job);
            }
        }
    }

    public void setStore(RdbStore store) {
        this.store = store;
    }
}
