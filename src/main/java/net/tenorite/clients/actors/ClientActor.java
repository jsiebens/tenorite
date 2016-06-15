package net.tenorite.clients.actors;

import akka.actor.Props;
import net.tenorite.clients.ClientChannel;
import net.tenorite.core.Tempo;
import net.tenorite.protocol.Message;
import net.tenorite.protocol.PlayerNumMessage;
import net.tenorite.protocol.PlineMessage;
import net.tenorite.util.AbstractActor;

import java.util.Arrays;

final class ClientActor extends AbstractActor {

    static Props props(Tempo tempo, String name, ClientChannel channel) {
        return Props.create(ClientActor.class, tempo, name, channel);
    }

    private final Tempo tempo;

    private final String name;

    private final ClientChannel channel;

    public ClientActor(Tempo tempo, String name, ClientChannel channel) {
        this.tempo = tempo;
        this.name = name;
        this.channel = channel;
    }

    @Override
    public void preStart() throws Exception {
        write(
            PlayerNumMessage.of(1),
            PlineMessage.of(""),
            PlineMessage.of("Welcome on Tenorite TetriNET Server!"),
            PlineMessage.of("")
        );
    }

    @Override
    public void postStop() throws Exception {
        channel.close();
    }

    @Override
    public void onReceive(Object message) throws Exception {
    }

    private void write(Message... messages) {
        Arrays
            .stream(messages)
            .forEach(channel::write);
    }

}

