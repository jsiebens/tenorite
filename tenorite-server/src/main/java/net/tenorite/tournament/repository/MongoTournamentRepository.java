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

import net.tenorite.core.Tempo;
import net.tenorite.game.GameModeId;
import net.tenorite.tournament.Tournament;
import net.tenorite.tournament.TournamentRepository;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * @author Johan Siebens
 */
@Component
public final class MongoTournamentRepository implements TournamentRepository {

    private final Jongo jongo;

    private final Map<Tempo, MongoTournamentOps> collections = new EnumMap<>(Tempo.class);

    @Autowired
    public MongoTournamentRepository(Jongo jongo) {
        this.jongo = jongo;
    }

    @Override
    public TournamentOps tournamentOps(Tempo tempo) {
        return collections.computeIfAbsent(tempo, t -> new MongoTournamentOps(tournamentCollection(jongo, tempo)));
    }

    private static class MongoTournamentOps implements TournamentOps {

        private final MongoCollection tournament;

        MongoTournamentOps(MongoCollection tournament) {
            this.tournament = tournament;
        }

        @Override
        public Tournament saveTournament(Tournament tournament) {
            this.tournament.save(tournament);
            return tournament;
        }

        @Override
        public List<Tournament> listTournaments(GameModeId gameModeId) {
            Iterable<Tournament> cursor = tournament.find("{gameModeId:#}", gameModeId).as(Tournament.class);
            return stream(cursor.spliterator(), false).collect(toList());
        }

    }

    public static MongoCollection tournamentCollection(Jongo jongo, Tempo tempo) {
        return jongo.getCollection(tempo + ":tournament");
    }

}
