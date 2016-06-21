package net.tenorite.util;

public class DeterministicStopWatch implements StopWatch {

    private long time;

    private boolean started = false;

    private boolean paused = false;

    @Override
    public void start() {
        started = true;
    }

    @Override
    public void suspend() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void stop() {
        started = false;
        paused = false;
    }

    @Override
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isSuspended() {
        return paused;
    }

    @Override
    public long getStartTime() {
        return time;
    }

}
