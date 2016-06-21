package net.tenorite.channel.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import net.tenorite.channel.Channel;
import net.tenorite.channel.Channels;
import net.tenorite.channel.commands.CreateChannel;
import net.tenorite.channel.commands.ListChannels;
import net.tenorite.channel.commands.ReserveSlot;
import net.tenorite.channel.events.SlotReservationFailed;
import net.tenorite.core.Tempo;
import net.tenorite.game.GameMode;
import net.tenorite.util.AbstractActor;
import scala.Option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static akka.actor.ActorRef.noSender;
import static java.util.stream.IntStream.range;

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

        range(1, 6).mapToObj(i -> "tetrinet:" + i).forEach(m -> self().tell(CreateChannel.of(Tempo.NORMAL, GameMode.CLASSIC, m), noSender()));
        range(1, 6).mapToObj(i -> "tetrifast:" + i).forEach(m -> self().tell(CreateChannel.of(Tempo.FAST, GameMode.CLASSIC, m), noSender()));

        range(1, 2).mapToObj(i -> "pure:" + i).forEach(m -> self().tell(CreateChannel.of(Tempo.NORMAL, GameMode.PURE, m), noSender()));
        range(1, 2).mapToObj(i -> "pure:" + i).forEach(m -> self().tell(CreateChannel.of(Tempo.FAST, GameMode.PURE, m), noSender()));
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof CreateChannel) {
            CreateChannel cc = (CreateChannel) o;
            actors.get(cc.getTempo()).forward(o, context());
        }
        else if (o instanceof ReserveSlot) {
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
        public void onReceive(Object o) throws Exception {
            if (o instanceof CreateChannel) {
                createChannel((CreateChannel) o);
            }
            else if (o instanceof ReserveSlot) {
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

        private void createChannel(CreateChannel c) {
            if (context().child(c.getName()).isEmpty()) {
                context().actorOf(ChannelActor.props(tempo, c.getGameMode(), c.getName()), c.getName());
                channels.add(Channel.of(c.getGameMode(), c.getName()));
            }
        }
    }

}
