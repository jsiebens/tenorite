package net.tenorite.util;

public final class CommonsStopWatch implements StopWatch {

    private final org.apache.commons.lang3.time.StopWatch internal = new org.apache.commons.lang3.time.StopWatch();

    @Override
    public void start() {
        internal.reset();
        internal.start();
    }

    @Override
    public void suspend() {
        internal.suspend();
    }

    @Override
    public void resume() {
        internal.resume();
    }

    @Override
    public void stop() {
        internal.stop();
    }

    @Override
    public long getTime() {
        return internal.getTime();
    }

    @Override
    public boolean isStarted() {
        return internal.isStarted();
    }

    @Override
    public boolean isSuspended() {
        return internal.isSuspended();
    }

    @Override
    public long getStartTime() {
        return internal.getStartTime();
    }
    
}
