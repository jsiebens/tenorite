package net.tenorite.channel.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import net.tenorite.channel.Channel;
import net.tenorite.channel.Channels;
import net.tenorite.channel.commands.ListChannels;
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
        else if (o instanceof ListChannels) {
            ListChannels lc = (ListChannels) o;
            actors.get(lc.getTempo()).forward(o, context());
        }
    }

    private static class ChannelsPerModeActor extends AbstractActor {

        private final Tempo tempo;

        private final List<Channel> channels = new ArrayList<>();

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
            else if (o instanceof ListChannels) {
                handleListChannels();
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

        private void handleListChannels() {
            replyWith(Channels.of(channels));
        }

        private void createChannel(String name) {
            if (context().child(name).isEmpty()) {
                context().actorOf(ChannelActor.props(tempo, name), name);
                channels.add(Channel.of(name));
            }
        }
    }

}
