package net.tenorite.util;

/**
 * @author Johan Siebens
 */
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
