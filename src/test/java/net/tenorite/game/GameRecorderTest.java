package net.tenorite.game;

import net.tenorite.core.Special;
import net.tenorite.core.Tempo;
import net.tenorite.modes.classic.Classic;
import net.tenorite.protocol.*;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static net.tenorite.game.GameRules.defaultGameRules;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class GameRecorderTest {

    private static final Tempo TEMPO = Tempo.NORMAL;

    @Test
    public void testGameRecorderRecorderShouldBeFinishedWhenEverybodyHasLeft() {
        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);

        GameRecorder recorder = new GameRecorder(TEMPO, Classic.ID, defaultGameRules(), gameListener());

        recorder.start(asList(playerA, playerB));
        assertThat(recorder.onPlayerLeaveMessage(PlayerLeaveMessage.of(1)).isPresent()).isFalse();
        assertThat(recorder.onPlayerLeaveMessage(PlayerLeaveMessage.of(2)).isPresent()).isTrue();
    }

    @Test
    public void testGameRecorderShouldBeFinishedWhenAllButOneSoloPlayerIsLost() {
        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);
        Player playerC = Player.of(3, "C", null);

        GameRecorder recorder = new GameRecorder(TEMPO, Classic.ID, defaultGameRules(), gameListener());

        recorder.start(asList(playerA, playerB, playerC));

        assertThat(recorder.onPlayerLostMessage(PlayerLostMessage.of(2)).isPresent()).isFalse();
        assertThat(recorder.onPlayerLostMessage(PlayerLostMessage.of(1)).isPresent()).isTrue();
    }

    @Test
    public void testGameRecorderShouldBeFinishedWhenAllPlayersLeftAreOnTheSameTeam() {
        Player playerA = Player.of(1, "nick", null);
        Player playerB = Player.of(2, "jane", "doe");
        Player playerC = Player.of(3, "john", "doe");

        GameRecorder recorder = new GameRecorder(TEMPO, Classic.ID, defaultGameRules(), gameListener());

        recorder.start(asList(playerA, playerB, playerC));

        assertThat(recorder.onPlayerLostMessage(PlayerLostMessage.of(1)).isPresent()).isTrue();
    }

    @Test
    public void testSinglePlayerGameShouldBeFinishedWhenPlayerIsLost() {
        Player playerA = Player.of(1, "A", null);

        GameRecorder recorder = new GameRecorder(TEMPO, Classic.ID, defaultGameRules(), gameListener());

        recorder.start(singletonList(playerA));

        assertThat(recorder.onPlayerLostMessage(PlayerLostMessage.of(1)).isPresent()).isTrue();
    }

    @Test
    public void testGameRecorderShouldKeepTrackOfFieldsAndKeepCompleteFieldMessages() {
        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);

        GameRecorder recorder = new GameRecorder(TEMPO, Classic.ID, defaultGameRules(), gameListener());

        recorder.start(asList(playerA, playerB));

        recorder.onFieldMessage(FieldMessage.of(1, "&8G9G9H:H"));
        recorder.onFieldMessage(FieldMessage.of(1, "#9E8F9F:F"));
        Game game = recorder.onPlayerLostMessage(PlayerLostMessage.of(1)).get();

        String recordedField = recorder.getField(1).get().getFieldString();
        String expectedField = Field.empty().update("&8G9G9H:H").update("#9E8F9F:F").getFieldString();

        assertThat(recordedField).isEqualTo(expectedField);

        String update1 = ((FieldMessage) game.getMessages().get(0).getMessage()).getUpdate();
        String update2 = ((FieldMessage) game.getMessages().get(1).getMessage()).getUpdate();

        assertThat(update1).isEqualTo(Field.empty().update("&8G9G9H:H").getFieldString());
        assertThat(update2).isEqualTo(expectedField);
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

        GameRecorder recorder = new GameRecorder(TEMPO, Classic.ID, defaultGameRules(), gameListener());

        recorder.start(asList(playerA, playerB));
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

    @Test
    public void testGameRecorderShouldRecordLvlMessages() {
        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);

        GameRecorder recorder = new GameRecorder(TEMPO, Classic.ID, defaultGameRules(), gameListener());

        recorder.start(asList(playerA, playerB));
        recorder.onLvlMessage(LvlMessage.of(1, 5));
        recorder.onLvlMessage(LvlMessage.of(2, 7));
        Game game = recorder.onPlayerLostMessage(PlayerLostMessage.of(1)).get();

        LvlMessage messageA = (LvlMessage) game.getMessages().get(0).getMessage();
        LvlMessage messageB = (LvlMessage) game.getMessages().get(1).getMessage();

        assertThat(messageA).isEqualTo(LvlMessage.of(1, 5));
        assertThat(messageB).isEqualTo(LvlMessage.of(2, 7));
    }

    @Test
    public void testGameRecorderShouldRecordSpecialBlockMessages() {
        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);

        GameRecorder recorder = new GameRecorder(TEMPO, Classic.ID, defaultGameRules(), gameListener());

        recorder.start(asList(playerA, playerB));
        recorder.onSpecialBlockMessage(SpecialBlockMessage.of(1, Special.ADDLINE, 2));
        recorder.onSpecialBlockMessage(SpecialBlockMessage.of(2, Special.QUAKEFIELD, 1));
        recorder.onSpecialBlockMessage(SpecialBlockMessage.of(2, Special.NUKEFIELD, 2));
        Game game = recorder.onPlayerLostMessage(PlayerLostMessage.of(1)).get();

        SpecialBlockMessage messageA = (SpecialBlockMessage) game.getMessages().get(0).getMessage();
        SpecialBlockMessage messageB = (SpecialBlockMessage) game.getMessages().get(1).getMessage();
        SpecialBlockMessage messageC = (SpecialBlockMessage) game.getMessages().get(2).getMessage();

        assertThat(messageA).isEqualTo(SpecialBlockMessage.of(1, Special.ADDLINE, 2));
        assertThat(messageB).isEqualTo(SpecialBlockMessage.of(2, Special.QUAKEFIELD, 1));
        assertThat(messageC).isEqualTo(SpecialBlockMessage.of(2, Special.NUKEFIELD, 2));
    }


    @Test
    public void testGameRecorderShouldAlwaysStartWithClassicRulesEnabledToTrackClassics() {
        Player playerA = Player.of(1, "A", null);

        GameRecorder recorder = new GameRecorder(TEMPO, Classic.ID, defaultGameRules(), gameListener());

        assertThat(recorder.start(singletonList(playerA)).getClassicRules()).isTrue();
    }

    @Test
    public void testGameRecorderShouldBlockClassicSpecialsIfClassicRulesAreDisabled() {
        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);

        GameRecorder recorder = new GameRecorder(TEMPO, Classic.ID, defaultGameRules(), gameListener());

        assertThat(recorder.start(asList(playerA, playerB)).getClassicRules()).isTrue();
        assertThat(recorder.onClassicStyleAddMessage(ClassicStyleAddMessage.of(1, 1))).isFalse();
        assertThat(recorder.onClassicStyleAddMessage(ClassicStyleAddMessage.of(1, 2))).isFalse();
        assertThat(recorder.onClassicStyleAddMessage(ClassicStyleAddMessage.of(1, 4))).isFalse();

        Game game = recorder.onPlayerLostMessage(PlayerLostMessage.of(1)).get();

        ClassicStyleAddMessage messageA = (ClassicStyleAddMessage) game.getMessages().get(0).getMessage();
        ClassicStyleAddMessage messageB = (ClassicStyleAddMessage) game.getMessages().get(1).getMessage();
        ClassicStyleAddMessage messageC = (ClassicStyleAddMessage) game.getMessages().get(2).getMessage();

        assertThat(messageA).isEqualTo(ClassicStyleAddMessage.of(1, 1));
        assertThat(messageB).isEqualTo(ClassicStyleAddMessage.of(1, 2));
        assertThat(messageC).isEqualTo(ClassicStyleAddMessage.of(1, 4));
    }

    @Test
    public void testGameRecorderShouldAllowClassicSpecialIfClassicRulesAreDisabledButSenderIsTheServer() {
        Player playerA = Player.of(1, "A", null);
        Player playerB = Player.of(2, "B", null);

        GameRecorder recorder = new GameRecorder(TEMPO, Classic.ID, defaultGameRules(), gameListener());

        assertThat(recorder.start(asList(playerA, playerB)).getClassicRules()).isTrue();
        assertThat(recorder.onClassicStyleAddMessage(ClassicStyleAddMessage.of(0, 1))).isTrue();
        assertThat(recorder.onClassicStyleAddMessage(ClassicStyleAddMessage.of(0, 2))).isTrue();
        assertThat(recorder.onClassicStyleAddMessage(ClassicStyleAddMessage.of(0, 4))).isTrue();
    }

    private GameListener gameListener() {
        return GameListener.NOOP;
    }

}
