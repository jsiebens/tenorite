package net.tenorite.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import net.tenorite.core.Special;
import net.tenorite.protocol.SpecialBlockMessage;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GameMessageTest {

    private ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module());

    @Test
    public void testJson() throws Exception {
        GameMessage expected = GameMessage.of(1000, SpecialBlockMessage.of(1, Special.ADDLINE, 2));
        String s = mapper.writeValueAsString(expected);
        assertThat(mapper.readValue(s, GameMessage.class)).isEqualTo(expected);
    }

}
