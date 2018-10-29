package io.sdmq.util;

/**
 * Created by Xs.Tao on 2017/7/21.
 */
public final class BlockUtils {

    public static final long DEF_SLEEP_TIMES = 100L;

    public static void waitingShortTime() {
        sleep(DEF_SLEEP_TIMES);
    }

    public static void sleep(final long millis, final boolean isInterrupt) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException ex) {
            if (isInterrupt) {
                Thread.currentThread().interrupt();
            }

        }
    }

    public static void sleep(final long millis) {
        sleep(millis, false);
    }
}
