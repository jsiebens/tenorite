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
package net.tenorite.tournament.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import net.tenorite.channel.commands.ReserveSlot;
import net.tenorite.channel.commands.Spectate;
import net.tenorite.channel.events.SlotReservationFailed;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameMode;
import net.tenorite.game.GameModes;
import net.tenorite.tournament.TournamentMatch;
import net.tenorite.tournament.TournamentRepository;
import net.tenorite.util.AbstractActor;
import scala.Option;

import java.util.Optional;

/**
 * @author Johan Siebens
 */
final class TournamentChannelsActor extends AbstractActor {

    public static Props props(Tempo tempo, GameModes gameModes, TournamentRepository ladderTournamentRepository) {
        return Props.create(TournamentChannelsActor.class, tempo, gameModes, ladderTournamentRepository);
    }

    private final Tempo tempo;

    private final GameModes gameModes;

    private final TournamentRepository tournamentRepository;

    public TournamentChannelsActor(Tempo tempo, GameModes gameModes, TournamentRepository tournamentRepository) {
        this.tempo = tempo;
        this.gameModes = gameModes;
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof ReserveSlot) {
            handleReserveSlot((ReserveSlot) o);
        }
        else if (o instanceof Spectate) {
            handleSpectate((Spectate) o);
        }
    }

    private void handleReserveSlot(ReserveSlot o) {
        Option<ActorRef> channel = context().child(o.getChannel());

        if (channel.isDefined()) {
            channel.get().forward(o, context());
        }
        else {
            Optional<TournamentMatch> opt = getTournamentMatch(o.getChannel());
            //Optional<ActorRef> opt = createChannel(o.getChannel());
            if (opt.isPresent()) {
                TournamentMatch match = opt.get();
                if (match.getState().equals(TournamentMatch.State.FINISHED)) {
                    replyWith(SlotReservationFailed.matchAlreadyFinished());
                }
                else if (match.getState().equals(TournamentMatch.State.BLOCKED)) {
                    replyWith(SlotReservationFailed.matchStillBlocked());
                }
                else {
                    GameMode gameMode = gameModes.find(match.getGameModeId()).orElseThrow(IllegalArgumentException::new);
                    context().actorOf(TournamentChannelActor.props(tempo, gameMode, match), match.getId());
                }
            }
            else {
                replyWith(SlotReservationFailed.channelNotAvailable());
            }
        }
    }

    private void handleSpectate(Spectate o) {
        Option<ActorRef> channel = context().child(o.getChannel());

        if (channel.isDefined()) {
            channel.get().forward(o, context());
        }
        else {
            getTournamentMatch(o.getChannel()).ifPresent(match -> {
                GameMode gameMode = gameModes.find(match.getGameModeId()).orElseThrow(IllegalArgumentException::new);
                context().actorOf(TournamentChannelActor.props(tempo, gameMode, match), match.getId());
            });
        }
    }

    private Optional<TournamentMatch> getTournamentMatch(String id) {
        return tournamentRepository.tournamentOps(tempo).loadTournamentMatch(id);
    }

}
