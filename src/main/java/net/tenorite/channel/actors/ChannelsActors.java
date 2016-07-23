package net.tenorite.channel.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameModes;

import java.util.EnumMap;
import java.util.Map;

public final class ChannelsActors {

    private final Map<Tempo, ActorRef> actors = new EnumMap<>(Tempo.class);

    public ChannelsActors(ActorSystem actorSystem, GameModes gameModes) {
        for (Tempo tempo : Tempo.values()) {
            ActorRef ref = actorSystem.actorOf(ChannelsActor.props(tempo, gameModes));
            actors.put(tempo, ref);
        }
    }

    public ActorRef get(Tempo tempo) {
        return actors.get(tempo);
    }

}
