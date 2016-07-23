package net.tenorite.clients.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import net.tenorite.channel.actors.ChannelsActors;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameModes;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Johan Siebens
 */
public final class ClientsActors {

    private final Map<Tempo, ActorRef> actors = new EnumMap<>(Tempo.class);

    public ClientsActors(ActorSystem actorSystem, GameModes gameModes, ChannelsActors channelsActors) {
        for (Tempo tempo : Tempo.values()) {
            ActorRef ref = actorSystem.actorOf(ClientsActor.props(tempo, gameModes, channelsActors.get(tempo)));
            actors.put(tempo, ref);
        }
    }

    public ActorRef get(Tempo tempo) {
        return actors.get(tempo);
    }

}
