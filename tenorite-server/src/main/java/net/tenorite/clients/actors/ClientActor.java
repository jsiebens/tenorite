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
package net.tenorite.clients.actors;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;
import net.tenorite.channel.Channels;
import net.tenorite.channel.commands.*;
import net.tenorite.channel.events.*;
import net.tenorite.clients.MessageSink;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameMode;
import net.tenorite.game.GameModeId;
import net.tenorite.game.GameModes;
import net.tenorite.protocol.*;
import net.tenorite.util.AbstractActor;
import org.springframework.util.StringUtils;
import scala.concurrent.duration.FiniteDuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static akka.actor.ActorRef.noSender;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

/**
 * @author Johan Siebens
 */
final class ClientActor extends AbstractActor {

    private static final long MAX_IDLE_TIME = 1000 * 60 * 30; // 30 minutes

    private static final Object PING = new Object();

    static Props props(Tempo tempo, String name, MessageSink sink, GameModes gameModes, ActorRef channels) {
        return Props.create(ClientActor.class, tempo, name, sink, gameModes, channels, null);
    }

    static Props props(Tempo tempo, String name, MessageSink sink, GameModes gameModes, ActorRef channels, ActorRef tournamentChannels) {
        return Props.create(ClientActor.class, tempo, name, sink, gameModes, channels, tournamentChannels);
    }

    private final Tempo tempo;

    private final String name;

    private final MessageSink sink;

    private final GameModes gameModes;

    private final ActorRef channels;

    private final ActorRef tournamentChannels;

    private final Commands commands;

    private ActorRef channel;

    private Cancellable heartBeat;

    private long lastMessageTimestamp;

    public ClientActor(Tempo tempo, String name, MessageSink sink, GameModes gameModes, ActorRef channels, ActorRef tournamentChannels) {
        this.tempo = tempo;
        this.name = name;
        this.sink = sink;
        this.gameModes = gameModes;
        this.channels = channels;
        this.tournamentChannels = tournamentChannels;

        this.commands = new Commands()
            .register("/match", (i, s) -> joinTournamentChannel(s))
            .register("/join", (i, s) -> joinChannel(s))
            .register("/create", (i, s) -> createChannel(s))
            .register("/list", (i, s) -> channels.tell(ListChannels.instance(), self()))
            .register("/modes", (i, s) -> listGameModes())
            .register("/help", (i, s) -> showHelp())
            .register("/exit", (i, s) -> context().stop(self()));
    }

    private void joinChannel(String channel) {
        channels.tell(ReserveSlot.of(channel, name), self());
    }

    private void joinTournamentChannel(String channel) {
        ofNullable(tournamentChannels).ifPresent(c -> c.tell(ReserveSlot.of(channel, name), self()));
    }

    private void createChannel(String s) {
        String[] split = s.split("\\s+");
        if (split.length != 2) {
            write(PlineMessage.of("<red>invalid number of arguments</red>"));
        }
        else {
            channels.tell(CreateChannel.of(GameModeId.of(split[0]), split[1], true), self());
        }
    }

    private void showHelp() {
        write(
            PlineMessage.of(""),
            PlineMessage.of("<b>available commands:</b>"),
            PlineMessage.of(""),
            PlineMessage.of("   /list <gray>- list all available channels"),
            PlineMessage.of("   /create <mode id> <channel name> <gray>- create a new channel"),
            PlineMessage.of("   /join <channel name> <gray>- join an existing channel"),
            PlineMessage.of("   /modes <gray>- list all available game modes"),
            PlineMessage.of("   /help <gray>- show this list of commands"),
            PlineMessage.of("")
        );
    }

    private void listGameModes() {
        write(PlineMessage.of(""));
        write(PlineMessage.of("<b>modes:</b>"));
        write(PlineMessage.of(""));
        for (GameMode gameMode : gameModes) {
            write(PlineMessage.of(format("   %s<gray> - %s</gray> (id: %s)", gameMode.getTitle(tempo), gameMode.getDescription(tempo), gameMode.getId())));
        }
        write(PlineMessage.of(""));
        write(PlineMessage.of("<purple>(type <b>/create <id> <channel name></b> to create a new channel)</purple>"));
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        write(
            PlayerNumMessage.of(1),
            PlineMessage.of(""),
            PlineMessage.of("Welcome on <b>Tenorite TetriNET</b> Server!"),
            PlineMessage.of(""),
            PlineMessage.of("<i>Join or create a channel to start playing...</i>")
        );

        channels.tell(ListChannels.instance(), self());

        heartBeat = getContext().system().scheduler().schedule(
            FiniteDuration.create(10, TimeUnit.SECONDS),
            FiniteDuration.create(10, TimeUnit.SECONDS),
            self(),
            PING,
            context().dispatcher(),
            noSender());
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        heartBeat.cancel();
        sink.close();
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Inbound) {
            handleInbound((Inbound) o);
        }
        else if (o instanceof Message) {
            write((Message) o);
        }
        else if (o instanceof ChannelCreated) {
            ChannelCreated cc = (ChannelCreated) o;
            joinChannel(cc.getName());
        }
        else if (o instanceof ChannelCreationFailed) {
            ChannelCreationFailed ccf = (ChannelCreationFailed) o;
            switch (ccf.getType()) {
                case INVALID_NAME:
                    write(PlineMessage.of("<red>invalid channel name</red>"));
                    break;
                case INVALID_GAME_MODE:
                    write(PlineMessage.of("<red>invalid game mode</red>"));
                    break;
                case NAME_ALREADY_IN_USE:
                    write(PlineMessage.of("<red>channel name already in use</red>"));
                    break;
            }
        }
        else if (o instanceof SlotReserved) {
            if (channel != null) {
                this.channel.tell(LeaveChannel.instance(), self());
                this.channel = sender();
            }
            else {
                this.channel = sender();
                this.channel.tell(ConfirmSlot.instance(), self());
            }
        }
        else if (o instanceof SlotReservationFailed) {
            SlotReservationFailed srf = (SlotReservationFailed) o;
            switch (srf) {
                case CHANNEL_IS_FULL:
                    write(PlineMessage.of("channel is <b>FULL</b>"));
                    break;
                case CHANNEL_NOT_AVAILABLE:
                    write(PlineMessage.of("channel is <b>not available</b>"));
                    break;
                case MATCH_ALREADY_FINISHED:
                    write(PlineMessage.of("match is <b>already finished</b>"));
                    break;
                case MATCH_STILL_BLOCKED:
                    write(PlineMessage.of("match is <b>not yet scheduled</b>"));
                    break;
            }
        }
        else if (o instanceof ChannelLeft) {
            this.channel.tell(ConfirmSlot.instance(), self());
        }
        else if (o instanceof Channels) {
            handleChannels((Channels) o);
        }
        else if (o == PING) {
            if (idleTime() > MAX_IDLE_TIME) {
                context().stop(self());
            }
            else {
                write(NOOP);
            }
        }
    }

    private void handleInbound(Inbound o) {
        MessageParser.parse(o.getMessage()).ifPresent(m -> {
            lastMessageTimestamp = System.currentTimeMillis();
            if (!(m instanceof PlineMessage) || !commands.run((PlineMessage) m)) {
                ofNullable(channel).ifPresent(c -> c.tell(m, self()));
            }
        });
    }

    private void handleChannels(Channels o) {
        write(PlineMessage.of(""));
        write(PlineMessage.of("<b>channels:</b>"));
        write(PlineMessage.of(""));
        o.getChannels()
            .stream()
            .sorted((a, b) -> a.getName().compareTo(b.getName()))
            .forEach(c -> {
                GameMode gameMode = gameModes.find(c.getGameModeId()).orElseThrow(IllegalStateException::new);
                String description = ofNullable(gameMode.getDescription(tempo)).filter(StringUtils::hasText).map(s -> "<gray> - " + s + "</gray> ").orElse(" ");
                if (c.getNrOfPlayers() < 6) {
                    write(PlineMessage.of(format("   %s%s <blue>(%s/%s)</blue>", c.getName(), description, c.getNrOfPlayers(), 6)));
                }
                else {
                    write(PlineMessage.of(format("   %s%s <red>(FULL)</red>", c.getName(), description)));
                }
            });

        write(PlineMessage.of(""));
        write(PlineMessage.of("<purple>(type <b>/join <channel name></b> to join a channel)</purple>"));
    }

    private void write(Message... messages) {
        Arrays
            .stream(messages)
            .forEach(sink::write);
    }

    private long idleTime() {
        return System.currentTimeMillis() - lastMessageTimestamp;
    }

    private final class Commands {

        private final Map<String, BiConsumer<Integer, String>> commands = new HashMap<>();

        Commands register(String command, BiConsumer<Integer, String> consumer) {
            this.commands.put(command, consumer);
            return this;
        }

        boolean run(PlineMessage message) {
            String[] words = message.getMessage().split("\\s+");
            if (words.length > 0) {
                String first = words[0];
                if (first.startsWith("/")) {
                    if (commands.containsKey(first)) {
                        String parameters = message.getMessage().substring(first.length()).trim();
                        commands.get(first).accept(message.getSender(), parameters);
                    }
                    else {
                        write(PlineMessage.of("<red>unknown command '" + first + "'"));
                        showHelp();
                    }
                    return true;
                }
            }
            return false;
        }

    }

    private static final Message NOOP = tempo -> "";

}

