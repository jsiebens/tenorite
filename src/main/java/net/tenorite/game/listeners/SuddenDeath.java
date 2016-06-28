package net.tenorite.game.listeners;

import net.tenorite.game.GameListener;
import net.tenorite.game.Player;
import net.tenorite.protocol.ClassicStyleAddMessage;
import net.tenorite.protocol.GmsgMessage;
import net.tenorite.protocol.Message;
import net.tenorite.util.Scheduler;
import net.tenorite.util.Scheduler.Cancellable;
import net.tenorite.util.StopWatch;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

public final class SuddenDeath implements GameListener {

    private static final ClassicStyleAddMessage FOUR_LINES = ClassicStyleAddMessage.of(0, 4);

    private static final ClassicStyleAddMessage TWO_LINES = ClassicStyleAddMessage.of(0, 2);

    private static final ClassicStyleAddMessage ONE_LINE = ClassicStyleAddMessage.of(0, 1);

    public static final String START_MESSAGE_TEMPLATE = "SUDDEN DEATH enabled! %s line(s) added every %s second(s)";

    private final StopWatch stopWatch;

    private final int delay;

    private final int interval;

    private final int nrOfLines;

    private final Scheduler scheduler;

    private final Consumer<Message> channel;

    private Cancellable monitor;

    private Cancellable suddenDeath;

    private int warningDelay = 30;

    public SuddenDeath(int delay, int interval, int nrOfLines, Scheduler scheduler, Consumer<Message> channel) {
        this.delay = delay;
        this.interval = interval;
        this.nrOfLines = nrOfLines;
        this.stopWatch = scheduler.stopWatch();
        this.scheduler = scheduler;
        this.channel = channel;
        this.warningDelay = Math.min(30, this.delay);
    }

    @Override
    public void onStartGame(List<Player> players) {
        stopWatch.start();

        if (delay <= 0) {
            return;
        }

        this.monitor = null;
        this.suddenDeath = null;

        scheduleNext();
    }

    @Override
    public void onPauseGame() {
        stopWatch.suspend();
    }

    @Override
    public void onResumeGame() {
        stopWatch.resume();
    }

    @Override
    public void onEndGame() {
        stopWatch.stop();

        ofNullable(monitor).ifPresent(Cancellable::cancel);
        ofNullable(suddenDeath).ifPresent(Cancellable::cancel);
    }

    private void scheduleNext() {
        this.monitor = scheduler.scheduleOnce(250, TimeUnit.MILLISECONDS, this::monitor);
    }

    private void monitor() {
        long time = getTime();
        long delayInMillis = delay * 1000;

        if (time >= delayInMillis) {
            channel.accept(GmsgMessage.of(format(START_MESSAGE_TEMPLATE, nrOfLines, interval)));

            suddenDeath =
                scheduler.schedule(0, interval, TimeUnit.SECONDS, () -> {
                    int x = nrOfLines;
                    while (x >= 4) {
                        channel.accept(FOUR_LINES);
                        x = x - 4;
                    }
                    while (x >= 2) {
                        channel.accept(TWO_LINES);
                        x = x - 2;
                    }
                    while (x >= 1) {
                        channel.accept(ONE_LINE);
                        x = x - 1;
                    }
                });
        }
        else {
            if (warningDelay > 0) {
                long warningDelayInMillis = delayInMillis - (warningDelay * 1000);
                if (time >= warningDelayInMillis) {
                    channel.accept(GmsgMessage.of(format("WARNING! Sudden death will begin in %s seconds", warningDelay)));
                    warningDelay = -1;
                }
            }
            scheduleNext();
        }
    }

    protected final long getTime() {
        return stopWatch.getTime();
    }

}
