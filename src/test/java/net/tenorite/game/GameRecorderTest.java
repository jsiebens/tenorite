package net.tenorite.game;

import net.tenorite.core.Tempo;
import net.tenorite.protocol.FieldMessage;
import net.tenorite.protocol.Message;
import net.tenorite.protocol.PlayerLeaveMessage;
import net.tenorite.protocol.PlayerLostMessage;
import net.tenorite.util.DeterministicStopWatch;
import net.tenorite.util.Scheduler;
import net.tenorite.util.StopWatch;
import org.jmock.lib.concurrent.DeterministicScheduler;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class GameRecorderTest {

    private static final Tempo TEMPO = Tempo.NORMAL;

    private DeterministicScheduler deterministicScheduler = new DeterministicScheduler();

    private DeterministicStopWatch deterministicStopWatch = new DeterministicStopWatch();

    private Scheduler scheduler;

    @Before
    public void setUp() {
        this.scheduler = new InternalScheduler(deterministicScheduler, deterministicStopWatch);
    }

    @Test
    public void testGameRecorderRecorderShouldBeFinishedWhenEverybodyHasLeft() {
        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);

        GameRecorder recorder = new GameRecorder(TEMPO, GameMode.CLASSIC, asList(playerA, playerB), scheduler, noop());

        recorder.start();
        assertThat(recorder.onPlayerLeaveMessage(PlayerLeaveMessage.of(1)).isPresent()).isFalse();
        assertThat(recorder.onPlayerLeaveMessage(PlayerLeaveMessage.of(2)).isPresent()).isTrue();
    }

    @Test
    public void testGameRecorderShouldBeFinishedWhenAllButOneSoloPlayerIsLost() {
        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);
        Player playerC = Player.of(3, "C", null);

        GameRecorder recorder = new GameRecorder(TEMPO, GameMode.CLASSIC, asList(playerA, playerB, playerC), scheduler, noop());

        recorder.start();

        assertThat(recorder.onPlayerLostMessage(PlayerLostMessage.of(2)).isPresent()).isFalse();
        assertThat(recorder.onPlayerLostMessage(PlayerLostMessage.of(1)).isPresent()).isTrue();
    }

    @Test
    public void testGameRecorderShouldBeFinishedWhenAllPlayersLeftAreOnTheSameTeam() {
        Player playerA = Player.of(1, "nick", null);
        Player playerB = Player.of(2, "jane", "doe");
        Player playerC = Player.of(3, "john", "doe");

        GameRecorder recorder = new GameRecorder(TEMPO, GameMode.CLASSIC, asList(playerA, playerB, playerC), scheduler, noop());

        recorder.start();

        assertThat(recorder.onPlayerLostMessage(PlayerLostMessage.of(1)).isPresent()).isTrue();
    }

    @Test
    public void testSinglePlayerGameShouldBeFinishedWhenPlayerIsLost() {
        Player playerA = Player.of(1, "A", null);

        GameRecorder recorder = new GameRecorder(TEMPO, GameMode.CLASSIC, singletonList(playerA), scheduler, noop());

        recorder.start();

        assertThat(recorder.onPlayerLostMessage(PlayerLostMessage.of(1)).isPresent()).isTrue();
    }

    @Test
    public void testGameRecorderShouldKeepTrackOfFieldsAndKeepCompleteFieldMessages() {
        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);

        GameRecorder recorder = new GameRecorder(TEMPO, GameMode.CLASSIC, asList(playerA, playerB), scheduler, noop());

        recorder.start();

        recorder.onFieldMessage(FieldMessage.of(1, "&8G9G9H:H"));
        recorder.onFieldMessage(FieldMessage.of(1, "#9E8F9F:F"));
        Game game = recorder.onPlayerLostMessage(PlayerLostMessage.of(1)).get();

        String recordedField = recorder.getField(1).get().getFieldString();
        String expectedField = Field.empty().update("&8G9G9H:H").update("#9E8F9F:F").getFieldString();

        assertThat(recordedField).isEqualTo(expectedField);
    }

    @Test
    public void testGameRecorderShouldIgnoreFieldMessagesFromPlayersNotAvailableOnStart() {
        Field fieldA1 = Field.randomCompletedField();
        Field fieldA2 = Field.randomCompletedField();
        Field fieldA3 = Field.randomCompletedField();

        Field fieldB = Field.randomCompletedField();
        Field fieldC = Field.randomCompletedField();

        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);

        GameRecorder recorder = new GameRecorder(TEMPO, GameMode.CLASSIC, asList(playerA, playerB), scheduler, noop());

        recorder.start();
        recorder.onFieldMessage(FieldMessage.of(1, fieldA1.getFieldString()));
        recorder.onFieldMessage(FieldMessage.of(1, fieldA2.getFieldString()));
        recorder.onFieldMessage(FieldMessage.of(2, fieldB.getFieldString()));
        recorder.onFieldMessage(FieldMessage.of(1, fieldA3.getFieldString()));
        recorder.onFieldMessage(FieldMessage.of(3, fieldC.getFieldString()));
        Game game = recorder.onPlayerLostMessage(PlayerLostMessage.of(1)).get();

        assertThat(recorder.getField(1).get().getFieldString()).isEqualTo(fieldA3.getFieldString());
        assertThat(recorder.getField(2).get().getFieldString()).isEqualTo(fieldB.getFieldString());
        assertThat(recorder.getField(3).isPresent()).isFalse();
    }

    public Consumer<Message> noop() {
        return m -> {
        };
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
