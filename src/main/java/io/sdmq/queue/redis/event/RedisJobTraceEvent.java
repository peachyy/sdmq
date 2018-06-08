package io.sdmq.queue.redis.event;

import io.sdmq.queue.JobMsg;
import io.sdmq.util.RdbOperation;

/**
 * Created by Xs.Tao on 2017/7/19.
 */
public class RedisJobTraceEvent implements JobEvent {

    private JobMsg       jobMsg    = null;
    private RdbOperation operation = RdbOperation.UPDATE;

    public RedisJobTraceEvent(JobMsg jobMsg) {
        this.jobMsg = jobMsg;
    }

    public RedisJobTraceEvent(JobMsg jobMsg, RdbOperation operation) {
        this.jobMsg = jobMsg;
        this.operation = operation;
    }

    @Override
    public JobMsg getJob() {
        return jobMsg;
    }

    public RdbOperation getOperation() {
        return operation;
    }
}
