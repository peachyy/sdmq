package io.sdmq.queue.core;

import io.sdmq.common.extension.SPI;
import io.sdmq.exception.ConsumeQueueException;

/**
 * Created by Xs.Tao on 2018/3/17.
 */
@SPI("consoleCQ")
public interface ConsumeQueueProvider {

    void init();

    void consumer(Job job) throws ConsumeQueueException;

    void destory();
}
