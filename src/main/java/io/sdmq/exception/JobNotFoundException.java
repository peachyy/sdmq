package io.sdmq.exception;

/**
 * Created by Xs.Tao on 2017/7/19.
 */
public class JobNotFoundException extends DelayQueueException {

    public JobNotFoundException(String errorMessage, Object... args) {
        super(errorMessage, args);
    }
}
