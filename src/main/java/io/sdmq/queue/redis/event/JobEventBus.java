package io.sdmq.queue.redis.event;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Xs.Tao on 2017/7/19.
 */
public class JobEventBus {

    private EventBus      bus      = null;
    private AtomicBoolean register = new AtomicBoolean(false);

    private JobEventBus() {
        bus = new AsyncEventBus(MoreExecutors.newDirectExecutorService());
    }

    public static JobEventBus getInstance() {
        return LazyHolder.JEB;
    }

    public void register(JobEventListener listener) {
        if (register.compareAndSet(false, true)) {
            bus.register(listener);
        }

    }

    public void unregister(JobEventListener listener) {
        if (register.get() == true) {
            bus.unregister(listener);
        }
    }

    public void post(JobEvent event) {
        if (register.get() == true) {
            bus.post(event);
        }

    }

    private static class LazyHolder {

        private static final JobEventBus JEB = new JobEventBus();
    }

}
