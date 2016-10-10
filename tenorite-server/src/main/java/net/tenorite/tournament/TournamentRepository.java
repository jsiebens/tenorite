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
package net.tenorite.tournament;

import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;

import java.util.List;
import java.util.Optional;

/**
 * @author Johan Siebens
 */
public interface TournamentRepository {

    TournamentOps tournamentOps(Tempo tempo);

    interface TournamentOps {

        Tournament saveTournament(Tournament tournament);

        List<Tournament> listTournaments(GameModeId gameModeId);

        void incrScore(String tournamentId, String player);

        Optional<Tournament> loadTournament(String id);

        Optional<TournamentMatch> loadTournamentMatch(String id);

        TournamentMatch saveTournamentMatch(TournamentMatch match);

        List<TournamentMatch> saveTournamentMatch(List<TournamentMatch> matches);

        long nrOfUnfinishedMatches(String tournament, int round);

        void openTournamentRound(String tournament, int round);

        String nextTournamentId();

    }

}
