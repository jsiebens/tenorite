package net.tenorite.channel.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import net.tenorite.channel.Channel;
import net.tenorite.channel.ChannelBuilder;
import net.tenorite.channel.Channels;
import net.tenorite.channel.commands.CreateChannel;
import net.tenorite.channel.commands.ListChannels;
import net.tenorite.channel.commands.ReserveSlot;
import net.tenorite.channel.events.ChannelJoined;
import net.tenorite.channel.events.ChannelLeft;
import net.tenorite.channel.events.SlotReservationFailed;
import net.tenorite.core.Tempo;
import net.tenorite.modes.*;
import net.tenorite.util.AbstractActor;
import scala.Option;

import java.util.HashMap;
import java.util.Map;

import static akka.actor.ActorRef.noSender;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;

public class ChannelsActor extends AbstractActor {

    public static Props props() {
        return Props.create(ChannelsActor.class);
    }

    private final Map<Tempo, ActorRef> actors = new HashMap<>();

    @Override
    public void preStart() throws Exception {
        super.preStart();
        actors.put(Tempo.NORMAL, context().actorOf(Props.create(ChannelsPerModeActor.class), Tempo.NORMAL.name()));
        actors.put(Tempo.FAST, context().actorOf(Props.create(ChannelsPerModeActor.class), Tempo.FAST.name()));

        range(1, 6).mapToObj(i -> "tetrinet:" + i).forEach(m -> self().tell(CreateChannel.of(Tempo.NORMAL, new Classic(), m), noSender()));
        range(1, 6).mapToObj(i -> "tetrifast:" + i).forEach(m -> self().tell(CreateChannel.of(Tempo.FAST, new Classic(), m), noSender()));

        stream(Tempo.values()).forEach(t -> self().tell(CreateChannel.of(t, new Pure(), "pure"), noSender()));
        stream(Tempo.values()).forEach(t -> self().tell(CreateChannel.of(t, new SticksAndSquares(), "sns"), noSender()));
        stream(Tempo.values()).forEach(t -> self().tell(CreateChannel.of(t, new Jelly(), "jelly"), noSender()));
        stream(Tempo.values()).forEach(t -> self().tell(CreateChannel.of(t, new SevenOFour(), "7o4"), noSender()));

        subscribe(ChannelJoined.class);
        subscribe(ChannelLeft.class);
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
        else if (o instanceof ChannelJoined) {
            ChannelJoined cj = (ChannelJoined) o;
            actors.get(cj.getTempo()).forward(o, context());
        }
        else if (o instanceof ChannelLeft) {
            ChannelLeft cj = (ChannelLeft) o;
            actors.get(cj.getTempo()).forward(o, context());
        }
    }

    private static class ChannelsPerModeActor extends AbstractActor {

        private final Map<String, Channel> channels = new HashMap<>();

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
            else if (o instanceof ChannelJoined) {
                handleChannelJoined((ChannelJoined) o);
            }
            else if (o instanceof ChannelLeft) {
                handleChannelLeft((ChannelLeft) o);
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
            replyWith(Channels.of(channels.values()));
        }

        private void handleChannelJoined(ChannelJoined o) {
            channels.computeIfPresent(o.getChannel(), (n, c) -> new ChannelBuilder().from(c).nrOfPlayers(c.getNrOfPlayers() + 1).build());
        }

        private void handleChannelLeft(ChannelLeft o) {
            channels.computeIfPresent(o.getChannel(), (n, c) -> new ChannelBuilder().from(c).nrOfPlayers(c.getNrOfPlayers() - 1).build());
        }

        private void createChannel(CreateChannel c) {
            if (context().child(c.getName()).isEmpty()) {
                context().actorOf(ChannelActor.props(c.getTempo(), c.getGameMode(), c.getName()), c.getName());
                channels.put(c.getName(), Channel.of(c.getGameMode().getId(), c.getName()));
            }
        }
    }

}
