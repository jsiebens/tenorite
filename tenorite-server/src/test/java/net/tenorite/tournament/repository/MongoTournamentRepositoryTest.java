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
package net.tenorite.tournament.repository;

import net.tenorite.AbstractTenoriteServerTestCase;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;
import net.tenorite.tournament.Tournament;
import net.tenorite.tournament.TournamentRepository;
import org.jongo.Jongo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johan Siebens
 */
public class MongoTournamentRepositoryTest extends AbstractTenoriteServerTestCase {

    @Autowired
    private Jongo jongo;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Before
    public void clear() {
        stream(Tempo.values()).forEach(t -> MongoTournamentRepository.tournamentCollection(jongo, t).drop());
    }

    @Test
    public void testSaveAndListTournaments() {
        Tournament tournamentA = Tournament.of("tournament A", GameModeId.of("CLASSIC"), Arrays.asList("john", "jane", "nick"));
        Tournament tournamentB = Tournament.of("tournament B", GameModeId.of("CLASSIC"), Arrays.asList("jane", "nick", "john"));
        Tournament tournamentC = Tournament.of("tournament C", GameModeId.of("CLASSIC"), Arrays.asList("nick", "john", "jane"));
        Tournament tournamentD = Tournament.of("tournament D", GameModeId.of("SPRINT"), Arrays.asList("nick", "john", "jane"));

        tournamentRepository.tournamentOps(Tempo.NORMAL).saveTournament(tournamentA);
        tournamentRepository.tournamentOps(Tempo.FAST).saveTournament(tournamentB);
        tournamentRepository.tournamentOps(Tempo.FAST).saveTournament(tournamentC);
        tournamentRepository.tournamentOps(Tempo.FAST).saveTournament(tournamentD);

        assertThat(tournamentRepository.tournamentOps(Tempo.NORMAL).listTournaments(GameModeId.of("CLASSIC"))).containsExactly(tournamentA);
        assertThat(tournamentRepository.tournamentOps(Tempo.FAST).listTournaments(GameModeId.of("CLASSIC"))).containsExactly(tournamentB, tournamentC);
        assertThat(tournamentRepository.tournamentOps(Tempo.FAST).listTournaments(GameModeId.of("SPRINT"))).containsExactly(tournamentD);
    }


}
