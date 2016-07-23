package net.tenorite.util;

import java.util.concurrent.TimeUnit;

/**
 * @author Johan Siebens
 */
public interface Scheduler {

    interface Cancellable {

        void cancel();

    }

    Cancellable scheduleOnce(long delay, TimeUnit timeUnit, Runnable task);

    Cancellable schedule(long initial, long delay, TimeUnit timeUnit, Runnable task);

    StopWatch stopWatch();

}
