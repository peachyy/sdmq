package io.sdmq.exception;

/**
 * Created by Xs.Tao on 2017/7/18.
 */
public class DelayQueueException extends RuntimeException {

    private static final long serialVersionUID = 5018901344199973515L;

    public DelayQueueException(final String errorMessage, final Object... args) {
        super(String.format(errorMessage, args));
    }

    public DelayQueueException(final Throwable cause) {
        super(cause);
    }
}
