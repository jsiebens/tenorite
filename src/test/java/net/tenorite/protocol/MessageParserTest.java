package net.tenorite.protocol;

import net.tenorite.core.Special;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageParserTest {

    @Test
    public void testParsePlineMessage() {
        Optional<PlineMessage> result = MessageParser.parse("pline 1  hello world ").map(m -> (PlineMessage) m);

        assertThat(result).hasValueSatisfying(m -> {
            assertThat(m.getSender()).isEqualTo(1);
            assertThat(m.getMessage()).isEqualTo("hello world");
        });
    }

    @Test
    public void testParsePlineActMessage() {
        Optional<PlineActMessage> result = MessageParser.parse("plineact 1  hello world ").map(m -> (PlineActMessage) m);

        assertThat(result).hasValueSatisfying(m -> {
            assertThat(m.getSender()).isEqualTo(1);
            assertThat(m.getMessage()).isEqualTo("hello world");
        });
    }

    @Test
    public void testParseGmsgMessage() {
        Optional<GmsgMessage> result = MessageParser.parse("gmsg  hello world ").map(m -> (GmsgMessage) m);

        assertThat(result).hasValueSatisfying(m -> {
            assertThat(m.getMessage()).isEqualTo("hello world");
        });
    }

    @Test
    public void testParseTeamMessage() {
        Optional<TeamMessage> result = MessageParser.parse("team 4  hello world ").map(m -> (TeamMessage) m);

        assertThat(result).hasValueSatisfying(m -> {
            assertThat(m.getSender()).isEqualTo(4);
            assertThat(m.getTeam()).isEqualTo("hello world");
        });
    }

    @Test
    public void testParseLvlMessage() {
        Optional<LvlMessage> result = MessageParser.parse("lvl 4 12 ").map(m -> (LvlMessage) m);

        assertThat(result).hasValueSatisfying(m -> {
            assertThat(m.getSender()).isEqualTo(4);
            assertThat(m.getLevel()).isEqualTo(12);
        });
    }

    @Test
    public void testParseFieldMessage() {
        Optional<FieldMessage> result = MessageParser.parse("f 1 234SDFSDRG").map(m -> (FieldMessage) m);

        assertThat(result).hasValueSatisfying(m -> {
            assertThat(m.getSender()).isEqualTo(1);
            assertThat(m.getUpdate()).isEqualTo("234SDFSDRG");
        });
    }

    @Test
    public void testParseFieldMessageWithEmptyUpdate() {
        Optional<FieldMessage> result = MessageParser.parse("f 3 ").map(m -> (FieldMessage) m);

        assertThat(result).hasValueSatisfying(m -> {
            assertThat(m.getSender()).isEqualTo(3);
            assertThat(m.getUpdate()).isEqualTo("");
        });
    }

    @Test
    public void testParseSpecialBlockMessage() {
        Optional<SpecialBlockMessage> result = MessageParser.parse("sb 1 a 2").map(m -> (SpecialBlockMessage) m);

        assertThat(result).hasValueSatisfying(m -> {
            assertThat(m.getSender()).isEqualTo(2);
            assertThat(m.getTarget()).isEqualTo(1);
            assertThat(m.getSpecial()).isEqualTo(Special.ADDLINE);
        });
    }

    @Test
    public void testParseUnknownSpecialBlockMessage() {
        Optional<SpecialBlockMessage> result = MessageParser.parse("sb 1 X 2").map(m -> (SpecialBlockMessage) m);

        assertThat(result).isEmpty();
    }

    @Test
    public void testParseClassicStyleAddMessage() {
        assertThat(MessageParser.parse("sb 0 cs1 2").map(m1 -> (ClassicStyleAddMessage) m1)).hasValueSatisfying(m -> {
            assertThat(m.getSender()).isEqualTo(2);
            assertThat(m.getLines()).isEqualTo(1);
        });

        assertThat(MessageParser.parse("sb 0 cs2 2").map(m1 -> (ClassicStyleAddMessage) m1)).hasValueSatisfying(m -> {
            assertThat(m.getSender()).isEqualTo(2);
            assertThat(m.getLines()).isEqualTo(2);
        });

        assertThat(MessageParser.parse("sb 0 cs4 2").map(m1 -> (ClassicStyleAddMessage) m1)).hasValueSatisfying(m -> {
            assertThat(m.getSender()).isEqualTo(2);
            assertThat(m.getLines()).isEqualTo(4);
        });
    }

    @Test
    public void testParseUnknownClassicStyleAddMessage() {
        assertThat(MessageParser.parse("sb 0 cs7 2")).isEmpty();
        assertThat(MessageParser.parse("sb 0 xxx 2")).isEmpty();
    }

    @Test
    public void testParseStartGameMessage() {
        Optional<StartGameMessage> result = MessageParser.parse("startgame 1 3").map(m -> (StartGameMessage) m);

        assertThat(result).hasValueSatisfying(m -> {
            assertThat(m.getSender()).isEqualTo(3);
        });
    }

    @Test
    public void testParseStopGameMessage() {
        Optional<StopGameMessage> result = MessageParser.parse("startgame 0 3").map(m -> (StopGameMessage) m);

        assertThat(result).hasValueSatisfying(m -> {
            assertThat(m.getSender()).isEqualTo(3);
        });
    }

    @Test
    public void testParsePauseGameMessage() {
        Optional<PauseGameMessage> result = MessageParser.parse("pause 1 3").map(m -> (PauseGameMessage) m);

        assertThat(result).hasValueSatisfying(m -> {
            assertThat(m.getSender()).isEqualTo(3);
        });
    }

    @Test
    public void testParseResumeGameMessage() {
        Optional<ResumeGameMessage> result = MessageParser.parse("pause 0 3").map(m -> (ResumeGameMessage) m);

        assertThat(result).hasValueSatisfying(m -> {
            assertThat(m.getSender()).isEqualTo(3);
        });
    }

    @Test
    public void testParsePlayerLostMessage() {
        Optional<PlayerLostMessage> result = MessageParser.parse("playerlost 3").map(m -> (PlayerLostMessage) m);

        assertThat(result).hasValueSatisfying(m -> {
            assertThat(m.getSender()).isEqualTo(3);
        });
    }

    @Test
    public void testParsePlayerLeaveMessage() {
        Optional<PlayerLeaveMessage> result = MessageParser.parse("playerleave 3").map(m -> (PlayerLeaveMessage) m);

        assertThat(result).hasValueSatisfying(m -> {
            assertThat(m.getSender()).isEqualTo(3);
        });
    }

    @Test
    public void testParseUnknownMessage() {
        Optional<Message> result = MessageParser.parse("unknown 1 x yz");
        assertThat(result).isEmpty();
    }

}
