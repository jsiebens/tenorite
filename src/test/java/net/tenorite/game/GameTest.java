package net.tenorite.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import net.tenorite.core.Special;
import net.tenorite.core.Tempo;
import net.tenorite.protocol.ClassicStyleAddMessage;
import net.tenorite.protocol.LvlMessage;
import net.tenorite.protocol.SpecialBlockMessage;
import org.junit.Test;

import java.util.Arrays;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class GameTest {

    private ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module()).registerModule(new GuavaModule());

    @Test
    public void testJson() throws Exception {
        Game expected =
            Game.of("game1", 1000, 250, Tempo.NORMAL, GameMode.CLASSIC,
                asList(
                    Player.of(1, "john", "doe"),
                    Player.of(2, "jane", "doe"),
                    Player.of(3, "nick", null)
                ),
                asList(
                    GameMessage.of(50, SpecialBlockMessage.of(1, Special.ADDLINE, 3)),
                    GameMessage.of(75, ClassicStyleAddMessage.of(2, 4)),
                    GameMessage.of(100, LvlMessage.of(2, 4))
                )
            );

        String s = mapper.writeValueAsString(expected);
        assertThat(mapper.readValue(s, Game.class)).isEqualTo(expected);
    }

}
