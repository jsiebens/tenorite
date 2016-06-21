package net.tenorite.util;

public interface StopWatch {

    void start();

    void suspend();

    void resume();

    void stop();

    long getTime();

    boolean isStarted();

    boolean isSuspended();

    long getStartTime();
    
}
