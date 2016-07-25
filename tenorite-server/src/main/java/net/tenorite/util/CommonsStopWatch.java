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
