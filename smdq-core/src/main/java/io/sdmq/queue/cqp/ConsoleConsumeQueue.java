package io.sdmq.queue.cqp;

import io.sdmq.common.extension.ExtNamed;
import io.sdmq.exception.ConsumeQueueException;
import io.sdmq.queue.core.ConsumeQueueProvider;
import io.sdmq.queue.core.Job;
import io.sdmq.util.FastJsonConvert;

/**
 * Created by Xs.Tao on 2018/3/17.
 */
@ExtNamed("consoleCQ")
public class ConsoleConsumeQueue implements ConsumeQueueProvider {

    @Override
    public void init() {

    }

    @Override
    public void consumer(Job job) throws ConsumeQueueException {
        System.out.println(String.format("invoke topic %s json:%s", job.getTopic(),
                FastJsonConvert.convertObjectToJSON(job)));
    }

    @Override
    public void destory() {

    }
}
