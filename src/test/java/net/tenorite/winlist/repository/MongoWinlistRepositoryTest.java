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
package net.tenorite.winlist.repository;

import net.tenorite.AbstractTenoriteServerTestCase;
import net.tenorite.core.Tempo;
import net.tenorite.modes.classic.Classic;
import net.tenorite.winlist.WinlistItem;
import net.tenorite.winlist.WinlistRepository;
import org.jongo.Jongo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;
import static net.tenorite.winlist.WinlistItem.Type.PLAYER;
import static net.tenorite.winlist.WinlistItem.Type.TEAM;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class MongoWinlistRepositoryTest extends AbstractTenoriteServerTestCase {

    @Autowired
    private Jongo jongo;

    @Autowired
    private WinlistRepository winlistRepository;

    @Before
    public void clear() {
        stream(Tempo.values()).forEach(t -> MongoWinlistRepository.createCollection(jongo, t).drop());
    }

    @Test
    public void testSaveAndLoadWinlistItems() {
        long now = System.currentTimeMillis();

        winlistRepository.winlistOps(Tempo.FAST).saveWinlistItem(Classic.ID, WinlistItem.of(PLAYER, "john", 12, now));
        winlistRepository.winlistOps(Tempo.FAST).saveWinlistItem(Classic.ID, WinlistItem.of(TEAM, "doe's", 97, now));
        winlistRepository.winlistOps(Tempo.NORMAL).saveWinlistItem(Classic.ID, WinlistItem.of(TEAM, "doe's", 102, now));
        winlistRepository.winlistOps(Tempo.FAST).saveWinlistItem(Classic.ID, WinlistItem.of(PLAYER, "jane", 43, now));

        assertThat(winlistRepository.winlistOps(Tempo.FAST).loadWinlist(Classic.ID))
            .containsExactly(
                WinlistItem.of(TEAM, "doe's", 97, now),
                WinlistItem.of(PLAYER, "jane", 43, now),
                WinlistItem.of(PLAYER, "john", 12, now)
            );

        assertThat(winlistRepository.winlistOps(Tempo.NORMAL).loadWinlist(Classic.ID))
            .containsExactly(
                WinlistItem.of(TEAM, "doe's", 102, now)
            );
    }

    @Test
    public void testSaveAndGetWinlistItem() {
        long now = System.currentTimeMillis();

        assertThat(winlistRepository.winlistOps(Tempo.FAST).getWinlistItem(Classic.ID, PLAYER, "john")).isEmpty();

        winlistRepository.winlistOps(Tempo.FAST).saveWinlistItem(Classic.ID, WinlistItem.of(PLAYER, "john", 12, now));

        assertThat(winlistRepository.winlistOps(Tempo.FAST).getWinlistItem(Classic.ID, PLAYER, "john")).hasValue(WinlistItem.of(PLAYER, "john", 12, now));
    }

    @Test
    public void testUpdateWinlistItem() {
        long now = System.currentTimeMillis();

        winlistRepository.winlistOps(Tempo.FAST).saveWinlistItem(Classic.ID, WinlistItem.of(PLAYER, "john", 12, now));
        winlistRepository.winlistOps(Tempo.FAST).saveWinlistItem(Classic.ID, WinlistItem.of(PLAYER, "john", 24, now - 10));
        winlistRepository.winlistOps(Tempo.FAST).saveWinlistItem(Classic.ID, WinlistItem.of(PLAYER, "john", 53, now - 20));

        assertThat(winlistRepository.winlistOps(Tempo.FAST).getWinlistItem(Classic.ID, PLAYER, "john"))
            .hasValue(WinlistItem.of(PLAYER, "john", 53, now - 20));

        assertThat(winlistRepository.winlistOps(Tempo.FAST).loadWinlist(Classic.ID))
            .containsExactly(
                WinlistItem.of(PLAYER, "john", 53, now - 20)
            );
    }

    @Test
    public void testIgnoreScoresOlderThanAMonth() {
        long now = System.currentTimeMillis();

        winlistRepository.winlistOps(Tempo.FAST).saveWinlistItem(Classic.ID, WinlistItem.of(PLAYER, "john", 12, now));
        winlistRepository.winlistOps(Tempo.FAST).saveWinlistItem(Classic.ID, WinlistItem.of(PLAYER, "nick", 24, now - MongoWinlistRepository.MONTH - 1));
        winlistRepository.winlistOps(Tempo.FAST).saveWinlistItem(Classic.ID, WinlistItem.of(PLAYER, "jane", 53, now - 20));

        assertThat(winlistRepository.winlistOps(Tempo.FAST).loadWinlist(Classic.ID))
            .containsExactly(
                WinlistItem.of(PLAYER, "jane", 53, now - 20),
                WinlistItem.of(PLAYER, "john", 12, now)
            );
    }

    @Test
    public void testWinlistIsLimitedTo20Items() {
        long now = System.currentTimeMillis();

        range(1, 31).forEach(i -> winlistRepository.winlistOps(Tempo.FAST).saveWinlistItem(Classic.ID, WinlistItem.of(PLAYER, "player" + i, i, now)));

        assertThat(winlistRepository.winlistOps(Tempo.FAST).loadWinlist(Classic.ID)).hasSize(20);
    }

}
