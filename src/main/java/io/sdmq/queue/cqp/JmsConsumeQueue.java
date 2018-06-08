package io.sdmq.queue.cqp;

import io.sdmq.common.autoconfigure.MessageProducer;
import io.sdmq.common.extension.ExtNamed;
import io.sdmq.exception.ConsumeQueueException;
import io.sdmq.queue.core.ConsumeQueueProvider;
import io.sdmq.queue.core.Job;

/**
 * Created by Xs.Tao on 2018/3/17.
 */
@ExtNamed("jmsCQ")
public class JmsConsumeQueue implements ConsumeQueueProvider {

    @Override
    public void init() {

    }

    @Override
    public void consumer(Job job) throws ConsumeQueueException {
        MessageProducer.send(job);
    }

    @Override
    public void destory() {

    }
}
