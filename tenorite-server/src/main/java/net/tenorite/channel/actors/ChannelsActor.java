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
package net.tenorite.channel.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import net.tenorite.channel.Channel;
import net.tenorite.channel.Channels;
import net.tenorite.channel.commands.CreateChannel;
import net.tenorite.channel.commands.ListChannels;
import net.tenorite.channel.commands.ReserveSlot;
import net.tenorite.channel.commands.Spectate;
import net.tenorite.channel.events.ChannelCreated;
import net.tenorite.channel.events.ChannelCreationFailed;
import net.tenorite.channel.events.SlotReservationFailed;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameMode;
import net.tenorite.game.GameModes;
import net.tenorite.util.AbstractActor;
import scala.Option;
import scala.concurrent.ExecutionContext;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static akka.dispatch.Futures.sequence;
import static akka.pattern.Patterns.ask;
import static java.util.stream.StreamSupport.stream;

/**
 * @author Johan Siebens
 */
final class ChannelsActor extends AbstractActor {

    public static Props props(Tempo tempo, GameModes gameModes) {
        return Props.create(ChannelsActor.class, tempo, gameModes);
    }

    private final Tempo tempo;

    private final GameModes gameModes;

    public ChannelsActor(Tempo tempo, GameModes gameModes) {
        this.tempo = tempo;
        this.gameModes = gameModes;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        for (GameMode gameMode : gameModes) {
            String name = gameMode.getId().toString().toLowerCase();
            context().actorOf(ChannelActor.props(tempo, gameMode, name, false), name);
        }
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof CreateChannel) {
            createChannel((CreateChannel) o);
        }
        else if (o instanceof ReserveSlot) {
            handleReserveSlot((ReserveSlot) o);
        }
        else if (o instanceof ListChannels) {
            handleListChannels((ListChannels) o);
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
            replyWith(SlotReservationFailed.channelNotAvailable());
        }
    }

    private void handleListChannels(ListChannels listChannels) {
        ActorRef sender = sender();
        ActorRef self = self();

        ExecutionContext ec = context().dispatcher();
        Iterable<ActorRef> children = getContext().getChildren();

        sequence(
            stream(children.spliterator(), false)
                .map(c -> ask(c, listChannels, 20).map(mapper(o -> (Channel) o), ec))
                .collect(Collectors.toList()),
            ec
        ).onSuccess(onSuccess(i -> sender.tell(Channels.of(i), self)), ec);
    }

    private void handleSpectate(Spectate o) {
        Option<ActorRef> channel = context().child(o.getChannel());

        if (channel.isDefined()) {
            channel.get().forward(o, context());
        }
    }

    private void createChannel(CreateChannel c) {
        if (context().child(c.getName()).isDefined()) {
            replyWith(ChannelCreationFailed.nameAlreadyInUse());
            return;
        }

        if (!isValid(c.getName())) {
            replyWith(ChannelCreationFailed.invalidName());
            return;
        }

        Optional<GameMode> optGameMode = gameModes.find(c.getGameModeId());

        if (!optGameMode.isPresent()) {
            replyWith(ChannelCreationFailed.invalidGameMode());
            return;
        }

        context().actorOf(ChannelActor.props(tempo, optGameMode.get(), c.getName(), c.isEphemeral()), c.getName());
        replyWith(ChannelCreated.of(tempo, c.getGameModeId(), c.getName()));
    }

    private boolean isValid(String name) {
        return name.length() >= 2 && name.length() <= 25 && Pattern.matches("[a-z0-9:_-]+", name);
    }

}
