package net.tenorite.channel.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import net.tenorite.channel.commands.ConfirmSlot;
import net.tenorite.channel.commands.LeaveChannel;
import net.tenorite.channel.commands.ReserveSlot;
import net.tenorite.channel.events.ChannelLeft;
import net.tenorite.channel.events.SlotReservationFailed;
import net.tenorite.channel.events.SlotReserved;
import net.tenorite.core.Tempo;
import net.tenorite.protocol.*;
import net.tenorite.util.AbstractActor;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static akka.actor.ActorRef.noSender;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

class ChannelActor extends AbstractActor {

    static Props props(Tempo tempo, String name) {
        return Props.create(ChannelActor.class, tempo, name);
    }

    private final Map<ActorRef, Slot> slots = new HashMap<>();

    private final Map<ActorRef, Slot> pending = new HashMap<>();

    private final AvailableSlots availableSlots = new AvailableSlots();

    private final Tempo tempo;

    private final String name;

    public ChannelActor(Tempo tempo, String name) {
        this.tempo = tempo;
        this.name = name;
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Message) {
            handleMessage(((Message) o));
        }
        else if (o instanceof ReserveSlot) {
            handleReserveSlot((ReserveSlot) o);
        }
        else if (o instanceof ConfirmSlot) {
            handleConfirmSlot();
        }
        else if (o instanceof LeaveChannel) {
            handleLeaveChannel(sender(), false);
        }
        else if (o instanceof Terminated) {
            handleLeaveChannel(((Terminated) o).actor(), true);
        }
    }

    private void handleMessage(Message o) {
        if (o instanceof PlineMessage) {
            handlePline((PlineMessage) o);
        }
        else if (o instanceof TeamMessage) {
            handleTeam((TeamMessage) o);
        }
    }

    private void handleReserveSlot(ReserveSlot o) {
        ActorRef sender = sender();

        if (!pending.containsKey(sender) && !slots.containsKey(sender)) {
            if (slots.size() + pending.size() < 6) {
                int slot = availableSlots.nextSlot();
                pending.put(sender(), new Slot(slot, o.getName(), sender));
                context().watch(sender);
                sender.tell(SlotReserved.instance(), self());
            }
            else {
                sender.tell(SlotReservationFailed.channelIsFull(), self());
            }
        }
    }

    private void handleConfirmSlot() {
        ActorRef sender = sender();
        Slot slot = pending.remove(sender);
        if (slot != null) {

            // send assigned slot number
            slot.send(PlayerNumMessage.of(slot.nr));

            // announce new player
            forEachSlot(p -> p.send(PlayerJoinMessage.of(slot.nr, slot.name)));

            // send current player list
            forEachSlot(p -> {
                slot.send(PlayerJoinMessage.of(p.nr, p.name));
                slot.send(TeamMessage.of(p.nr, ofNullable(p.team).orElse("")));
            });

            // send welcome message
            slot.send(PlineMessage.of(""));
            slot.send(PlineMessage.of(format("Hello %s, welcome in channel %s", slot.name, name)));
            slot.send(PlineMessage.of(""));

            slots.put(sender(), slot);
        }
    }

    private void handleLeaveChannel(ActorRef actor, boolean disconnected) {
        ofNullable(pending.remove(actor)).ifPresent(p -> availableSlots.releaseSlot(p.nr));

        Slot slot = slots.get(actor);

        if (slot != null) {

            if (!disconnected) {
                // clear player list
                forEachSlot(p -> slot.send(PlayerLeaveMessage.of(p.nr)));
            }

            // release slot
            availableSlots.releaseSlot(slot.nr);

            slots.remove(actor);

            // accounce leave in room
            forEachSlot(p -> p.send(PlayerLeaveMessage.of(slot.nr)));

            if (!disconnected) {
                actor.tell(ChannelLeft.of(tempo, name, slot.name), self());
                context().unwatch(sender());
            }
        }
    }

    private void handlePline(PlineMessage pline) {
        forEachSlot(p -> p.nr != pline.getSender(), p -> p.send(pline));
    }

    private void handleTeam(TeamMessage o) {
        forEachSlot(p -> {
            if (p.nr == o.getSender()) {
                p.team = o.getTeam();
            }
            else {
                p.send(o);
            }
        });
    }

    // =================================================================================================================

    private void forEachSlot(Consumer<Slot> playerConsumer) {
        slots.values().stream().forEach(playerConsumer);
    }

    private void forEachSlot(Predicate<Slot> predicate, Consumer<Slot> playerConsumer) {
        slots.values().stream().filter(predicate).forEach(playerConsumer);
    }

    private static final class AvailableSlots {

        private final LinkedList<Integer> slots;

        AvailableSlots() {
            this.slots = new LinkedList<>(range(1, 7).boxed().collect(toList()));
        }

        int nextSlot() {
            return slots.removeFirst();
        }

        void releaseSlot(int slot) {
            slots.add(slot);
            Collections.sort(slots);
        }

    }

    private static final class Slot {

        private final int nr;

        private final String name;

        private final ActorRef actor;

        private String team;

        Slot(int nr, String name, ActorRef actor) {
            this.nr = nr;
            this.name = name;
            this.actor = actor;
        }

        void send(Message m) {
            actor.tell(m, noSender());
        }

    }

}
