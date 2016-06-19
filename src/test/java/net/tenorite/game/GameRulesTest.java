package net.tenorite.game;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GameRulesTest {

    @Test
    public void testDefaultRules() {
        String s = GameRules.defaultGameRules().toString();
        assertThat(s).isEqualTo("0 0 2 1 1 1 18 1111111111111112222222222222223333333333333344444444444444555555555555556666666666666677777777777777 1111111111111111111111222222222222222222344444444444444445666666666666666777778888888888889999999999 1 0");
    }

}
