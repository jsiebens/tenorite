package net.tenorite.channel.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import net.tenorite.channel.commands.ReserveSlot;
import net.tenorite.channel.events.SlotReservationFailed;
import net.tenorite.core.Tempo;
import net.tenorite.util.AbstractActor;
import scala.Option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelsActor extends AbstractActor {

    public static Props props() {
        return Props.create(ChannelsActor.class);
    }

    private final Map<Tempo, ActorRef> actors = new HashMap<>();

    @Override
    public void preStart() throws Exception {
        super.preStart();
        actors.put(Tempo.NORMAL, context().actorOf(Props.create(ChannelsPerModeActor.class, Tempo.NORMAL), Tempo.NORMAL.name()));
        actors.put(Tempo.FAST, context().actorOf(Props.create(ChannelsPerModeActor.class, Tempo.FAST), Tempo.FAST.name()));
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof ReserveSlot) {
            ReserveSlot rs = (ReserveSlot) o;
            actors.get(rs.getTempo()).forward(o, context());
        }
    }

    private static class ChannelsPerModeActor extends AbstractActor {

        private final Tempo tempo;

        private final List<String> channels = new ArrayList<>();

        public ChannelsPerModeActor(Tempo tempo) {
            this.tempo = tempo;
        }

        @Override
        public void preStart() throws Exception {
            createChannel("channel:1");
            createChannel("channel:2");
            createChannel("channel:3");
        }

        @Override
        public void onReceive(Object o) throws Exception {
            if (o instanceof ReserveSlot) {
                handleReserveSlot((ReserveSlot) o);
            }
        }

        private void handleReserveSlot(ReserveSlot o) {
            Option<ActorRef> channel = context().child(o.getChannel());

            if (channel.isDefined()) {
                channel.get().forward(o, context());
            }
            else {
                sender().tell(SlotReservationFailed.channelNotAvailable(), self());
            }
        }

        private void createChannel(String name) {
            if (context().child(name).isEmpty()) {
                context().actorOf(ChannelActor.props(tempo, name), name);
                channels.add(name);
            }
        }
    }

}
