/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tenorite.util;

/**
 * @author Johan Siebens
 */
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
