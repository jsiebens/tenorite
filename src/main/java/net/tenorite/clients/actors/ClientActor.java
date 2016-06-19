package net.tenorite.clients.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import net.tenorite.channel.Channels;
import net.tenorite.channel.commands.ConfirmSlot;
import net.tenorite.channel.commands.LeaveChannel;
import net.tenorite.channel.commands.ListChannels;
import net.tenorite.channel.commands.ReserveSlot;
import net.tenorite.channel.events.ChannelLeft;
import net.tenorite.channel.events.SlotReservationFailed;
import net.tenorite.channel.events.SlotReserved;
import net.tenorite.clients.MessageSink;
import net.tenorite.core.Tempo;
import net.tenorite.protocol.*;
import net.tenorite.util.AbstractActor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Optional.ofNullable;

final class ClientActor extends AbstractActor {

    static Props props(Tempo tempo, String name, MessageSink sink, ActorRef channels) {
        return Props.create(ClientActor.class, tempo, name, sink, channels);
    }

    private final MessageSink sink;

    private final Commands commands;

    private ActorRef channel;

    public ClientActor(Tempo tempo, String name, MessageSink sink, ActorRef channels) {
        this.sink = sink;

        this.commands = new Commands()
            .register("/join", (i, s) -> channels.tell(ReserveSlot.of(tempo, s, name), self()))
            .register("/list", (i, s) -> channels.tell(ListChannels.of(tempo), self()))
            .register("/exit", (i, s) -> context().stop(self()));
    }

    @Override
    public void preStart() throws Exception {
        write(
            PlayerNumMessage.of(1),
            PlineMessage.of(""),
            PlineMessage.of("Welcome on Tenorite TetriNET Server!"),
            PlineMessage.of(""),
            PlineMessage.of("Join a channel to start playing..."),
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
                    write(PlineMessage.of("channel is FULL"));
                    break;
                case CHANNEL_NOT_AVAILABLE:
                    write(PlineMessage.of("channel is not available"));
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
            .forEach(c -> write(PlineMessage.of("   " + c.getName())));

        write(PlineMessage.of(""));
        write(PlineMessage.of("(type /join <name>)"));
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

