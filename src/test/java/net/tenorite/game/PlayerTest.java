package net.tenorite.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayerTest {

    private ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module());

    @Test
    public void testJson() throws Exception {
        Player expected = Player.of(1, "john", "doe");
        String s = mapper.writeValueAsString(expected);
        assertThat(mapper.readValue(s, Player.class)).isEqualTo(expected);
    }

    @Test
    public void testJsonWithoutTeam() throws Exception {
        Player expected = Player.of(1, "john", null);
        String s = mapper.writeValueAsString(expected);
        assertThat(mapper.readValue(s, Player.class)).isEqualTo(expected);
    }

}
