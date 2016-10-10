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
import net.tenorite.system.config.MongoCollections;
import net.tenorite.tournament.Tournament;
import net.tenorite.tournament.TournamentMatch;
import net.tenorite.tournament.TournamentRepository;
import net.tenorite.util.Base36;
import org.jongo.MongoCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * @author Johan Siebens
 */
@Component
public final class MongoTournamentRepository implements TournamentRepository {

    private final Map<Tempo, MongoTournamentOps> ops = new EnumMap<>(Tempo.class);

    @Autowired
    public MongoTournamentRepository(MongoCollections collections) {
        stream(Tempo.values()).forEach(t -> ops.put(t, createOps(t, collections)));
    }

    @Override
    public TournamentOps tournamentOps(Tempo tempo) {
        return ops.get(tempo);
    }

    private static MongoTournamentOps createOps(Tempo tempo, MongoCollections collections) {
        return new MongoTournamentOps(
            collections.getCollection(tempo, "tournament"),
            collections.getCollection(tempo, "tournament:match"),
            collections.getCollection(tempo, "counter")
        );
    }

    private static class MongoTournamentOps implements TournamentOps {

        private final MongoCollection tournaments;

        private final MongoCollection tournamentMatches;

        private final MongoCollection counters;

        MongoTournamentOps(MongoCollection tournaments, MongoCollection tournamentMatches, MongoCollection counters) {
            this.tournaments = tournaments;
            this.tournamentMatches = tournamentMatches;
            this.counters = counters;
        }

        @Override
        public Optional<TournamentMatch> loadTournamentMatch(String id) {
            return ofNullable(tournamentMatches.findOne("{_id:#}", id).as(TournamentMatch.class));
        }

        @Override
        public Optional<Tournament> loadTournament(String id) {
            return ofNullable(tournaments.findOne("{_id:{$oid:#}}", id).as(Tournament.class));
        }

        @Override
        public List<TournamentMatch> listTournamentMatches(String tournament) {
            Iterable<TournamentMatch> cursor = tournamentMatches.find("{tournament:#}", tournament).as(TournamentMatch.class);
            return stream(cursor.spliterator(), false).collect(toList());
        }

        @Override
        public Tournament saveTournament(Tournament tournament) {
            tournaments.save(tournament);
            return tournament;
        }

        @Override
        public List<Tournament> listTournaments(GameModeId gameModeId) {
            Iterable<Tournament> cursor = tournaments.find("{gameModeId:#}", gameModeId).as(Tournament.class);
            return stream(cursor.spliterator(), false).collect(toList());
        }

        @Override
        public void incrScore(String tournamentId, String player) {
            tournaments.update("{_id:{$oid:#}, 'participants.name':#}", tournamentId, player).with("{$inc:{'participants.$.score':1}}");
        }

        @Override
        public TournamentMatch saveTournamentMatch(TournamentMatch match) {
            tournamentMatches.save(match);
            return match;
        }

        @Override
        public List<TournamentMatch> saveTournamentMatch(List<TournamentMatch> matches) {
            matches.forEach(tournamentMatches::save);
            return matches;
        }

        @Override
        public long nrOfUnfinishedMatches(String tournament, int round) {
            return tournamentMatches.count("{tournament:#,round:#,state:{$ne:#}}", tournament, round, TournamentMatch.State.FINISHED);
        }

        @Override
        public void openTournamentRound(String tournament, int round) {
            tournamentMatches
                .update("{tournament:#,round:#,state:{$ne:#}}", tournament, round, TournamentMatch.State.FINISHED)
                .multi()
                .with("{$set:{state:#}}", TournamentMatch.State.SCHEDULED);
        }

        @Override
        public String nextTournamentId() {
            long value = Optional.ofNullable(
                counters
                    .findAndModify("{_id:#}", "tournament")
                    .with("{$inc : { value : 1}}")
                    .upsert()
                    .as(Counter.class)
            ).map(Counter::getValue).orElse(0L);

            return Base36.convert(value);
        }

    }

    private static class Counter {

        private long value;

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }

    }

}
