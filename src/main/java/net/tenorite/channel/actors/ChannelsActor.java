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
import net.tenorite.modes.*;
import net.tenorite.modes.classic.Classic;
import net.tenorite.util.AbstractActor;
import scala.Option;
import scala.concurrent.ExecutionContext;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static akka.actor.ActorRef.noSender;
import static akka.dispatch.Futures.sequence;
import static akka.pattern.Patterns.ask;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;
import static java.util.stream.StreamSupport.stream;

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
        stream(Tempo.values()).forEach(t -> self().tell(CreateChannel.of(t, new BreakOut(), "breakout"), noSender()));
        stream(Tempo.values()).forEach(t -> self().tell(CreateChannel.of(t, new GBomb(), "gbomb"), noSender()));
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

        @Override
        public void onReceive(Object o) throws Exception {
            if (o instanceof CreateChannel) {
                createChannel((CreateChannel) o);
            }
            else if (o instanceof ReserveSlot) {
                handleReserveSlot((ReserveSlot) o);
            }
            else if (o instanceof ListChannels) {
                handleListChannels((ListChannels) o);
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

        private void handleListChannels(ListChannels listChannels) {
            ActorRef sender = sender();
            ActorRef self = self();

            ExecutionContext ec = context().dispatcher();
            Iterable<ActorRef> children = getContext().getChildren();

            sequence(
                stream(children.spliterator(), false)
                    .map(c -> ask(c, listChannels, 20).map(mapper(o -> (Channel) o), ec))
                    .collect(Collectors.toList()),
                ec
            ).onSuccess(onSuccess(i -> sender.tell(Channels.of(i), self)), ec);
        }

        private void createChannel(CreateChannel c) {
            if (context().child(c.getName()).isEmpty()) {
                context().actorOf(ChannelActor.props(c.getTempo(), c.getGameMode(), c.getName()), c.getName());
            }
        }
    }

}
