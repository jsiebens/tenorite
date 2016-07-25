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
package net.tenorite.game.listeners;

import net.tenorite.protocol.ClassicStyleAddMessage;
import net.tenorite.protocol.GmsgMessage;
import net.tenorite.protocol.Message;
import net.tenorite.test.DeterministicStopWatch;
import net.tenorite.util.Scheduler;
import net.tenorite.util.StopWatch;
import org.jmock.lib.concurrent.DeterministicScheduler;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class SuddenDeathTest {

    @Test
    public void testWarningMessage() {
        DeterministicScheduler scheduler = new DeterministicScheduler();
        DeterministicStopWatch stopWatch = new DeterministicStopWatch();
        List<Message> messages = new ArrayList<>();

        SuddenDeath suddenDeath = new SuddenDeath(50, 5, 7, new InternalScheduler(scheduler, stopWatch), messages::add);
        suddenDeath.onStartGame(emptyList());

        stopWatch.setTime(20 * 1000);
        scheduler.tick(300, TimeUnit.MILLISECONDS);

        assertThat(messages).containsExactly(GmsgMessage.of("WARNING! Sudden death will begin in 30 seconds"));
    }

    @Test
    public void testStartSuddenDeath() {
        DeterministicScheduler scheduler = new DeterministicScheduler();
        DeterministicStopWatch stopWatch = new DeterministicStopWatch();
        List<Message> messages = new ArrayList<>();

        SuddenDeath suddenDeath = new SuddenDeath(50, 5, 11, new InternalScheduler(scheduler, stopWatch), messages::add);
        suddenDeath.onStartGame(emptyList());

        stopWatch.setTime(60 * 1000);
        scheduler.tick(300, TimeUnit.MILLISECONDS);
        scheduler.tick(5001, TimeUnit.MILLISECONDS);

        assertThat(messages).containsExactly(
            GmsgMessage.of(format(SuddenDeath.START_MESSAGE_TEMPLATE, 11, 5)),
            ClassicStyleAddMessage.of(0, 4),
            ClassicStyleAddMessage.of(0, 4),
            ClassicStyleAddMessage.of(0, 2),
            ClassicStyleAddMessage.of(0, 1),
            ClassicStyleAddMessage.of(0, 4),
            ClassicStyleAddMessage.of(0, 4),
            ClassicStyleAddMessage.of(0, 2),
            ClassicStyleAddMessage.of(0, 1)
        );
    }

    @Test
    public void testStopSuddenDeathBeforeDelay() {
        DeterministicScheduler scheduler = new DeterministicScheduler();
        DeterministicStopWatch stopWatch = new DeterministicStopWatch();
        List<Message> messages = new ArrayList<>();

        SuddenDeath suddenDeath = new SuddenDeath(50, 5, 11, new InternalScheduler(scheduler, stopWatch), messages::add);
        suddenDeath.onStartGame(emptyList());

        stopWatch.setTime(60 * 1000);
        scheduler.tick(300, TimeUnit.MILLISECONDS);

        suddenDeath.onEndGame();
        messages.clear();

        scheduler.tick(5001, TimeUnit.MILLISECONDS);

        assertThat(messages).isEmpty();
    }


    private static class InternalScheduler implements Scheduler {

        private final ScheduledExecutorService scheduler;

        private final StopWatch stopWatch;

        public InternalScheduler(ScheduledExecutorService scheduler, StopWatch stopWatch) {
            this.scheduler = scheduler;
            this.stopWatch = stopWatch;
        }

        @Override
        public Cancellable scheduleOnce(long delay, TimeUnit timeUnit, Runnable task) {
            ScheduledFuture<?> s = scheduler.schedule(task, delay, timeUnit);
            return () -> s.cancel(true);
        }

        @Override
        public Cancellable schedule(long initial, long delay, TimeUnit timeUnit, Runnable task) {
            ScheduledFuture<?> s = scheduler.scheduleAtFixedRate(task, initial, delay, timeUnit);
            return () -> s.cancel(true);
        }

        @Override
        public StopWatch stopWatch() {
            return stopWatch;
        }

    }

}
