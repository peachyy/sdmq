package io.sdmq.exception;

/**
 * Created by Xs.Tao on 2018/3/17.
 */
public class ConsumeQueueException extends DelayQueueException {

    public ConsumeQueueException(String errorMessage, Object... args) {
        super(errorMessage, args);
    }
}
