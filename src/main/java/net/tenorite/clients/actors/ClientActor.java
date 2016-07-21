package net.tenorite.clients.actors;

import akka.actor.ActorRef;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

final class ClientActor extends AbstractActor {

    static Props props(Tempo tempo, String name, MessageSink sink, GameModes gameModes, ActorRef channels) {
        return Props.create(ClientActor.class, tempo, name, sink, gameModes, channels);
    }

    private final Tempo tempo;

    private final String name;

    private final MessageSink sink;

    private final GameModes gameModes;

    private final ActorRef channels;

    private ActorRef channel;

    private final Commands commands;

    public ClientActor(Tempo tempo, String name, MessageSink sink, GameModes gameModes, ActorRef channels) {
        this.tempo = tempo;
        this.name = name;
        this.sink = sink;
        this.gameModes = gameModes;
        this.channels = channels;

        this.commands = new Commands()
            .register("/join", (i, s) -> joinChannel(s))
            .register("/list", (i, s) -> channels.tell(ListChannels.of(tempo), self()))
            .register("/create", (i, s) -> createChannel(s))
            .register("/exit", (i, s) -> context().stop(self()));
    }

    private void joinChannel(String channel) {
        channels.tell(ReserveSlot.of(tempo, channel, name), self());
    }

    private void createChannel(String s) {
        String[] split = s.split("\\s+");
        if (split.length != 2) {
            write(PlineMessage.of("<red>invalid number of arguments</red>"));
        }
        else {
            Optional<GameMode> gameMode = gameModes.find(GameModeId.of(split[0]));
            if (gameMode.isPresent()) {
                channels.tell(CreateChannel.of(tempo, gameMode.get(), split[1], true), self());
            }
            else {
                write(PlineMessage.of("<red>invalid game mode</red>"));
            }
        }
    }

    @Override
    public void preStart() throws Exception {
        write(
            PlayerNumMessage.of(1),
            PlineMessage.of(""),
            PlineMessage.of("Welcome on <b>Tenorite TetriNET</b> Server!"),
            PlineMessage.of(""),
            PlineMessage.of("<i>Join a channel to start playing...</i>"),
            PlineMessage.of("")
        );

        commands.run(PlineMessage.of("/list"));
    }

    @Override
    public void postStop() throws Exception {
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
            }
        }
        else if (o instanceof ChannelLeft) {
            this.channel.tell(ConfirmSlot.instance(), self());
        }
        else if (o instanceof Channels) {
            handleChannels((Channels) o);
        }
    }

    private void handleInbound(Inbound o) {
        MessageParser.parse(o.getMessage()).ifPresent(m -> {
            if (!(m instanceof PlineMessage) || !commands.run((PlineMessage) m)) {
                ofNullable(channel).ifPresent(c -> c.tell(m, self()));
            }
        });
    }

    private void handleChannels(Channels o) {
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
        write(PlineMessage.of("<gray>(type /join <name>)</gray>"));
    }

    private void write(Message... messages) {
        Arrays
            .stream(messages)
            .forEach(sink::write);
    }

    private static final class Commands {

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
                    return true;
                }
            }
            return false;
        }

    }

}

