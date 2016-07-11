package net.tenorite.modes.classic;

import net.tenorite.badges.Badge;
import net.tenorite.badges.BadgeRepository;
import net.tenorite.badges.BadgeValidator;
import net.tenorite.badges.events.BadgeEarned;
import net.tenorite.core.Special;
import net.tenorite.game.*;
import net.tenorite.game.events.GameFinished;
import net.tenorite.protocol.FieldMessage;
import net.tenorite.protocol.Message;
import net.tenorite.protocol.SpecialBlockMessage;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;

final class GrandTheft extends BadgeValidator {

    public static final int TARGET = 20;

    GrandTheft(Badge badge) {
        super(badge);
    }

    @Override
    protected void doProcess(GameFinished gameFinished, BadgeRepository.BadgeOps badgeOps, Consumer<BadgeEarned> onBadgeEarned) {
        Map<Integer, Player> allPlayers = gameFinished.getRanking().stream().map(PlayingStats::getPlayer).collect(Collectors.toMap(Player::getSlot, identity()));

        Game game = gameFinished.getGame();

        Map<Player, Integer> counts = new HashMap<>();
        Map<Integer, Integer> nrOfSpecials = new HashMap<>();

        for (GameMessage gameMessage : game.getMessages()) {
            Message message = gameMessage.getMessage();

            if (message instanceof FieldMessage) {
                FieldMessage fieldMessage = (FieldMessage) message;
                int specials = Field.of(fieldMessage.getUpdate()).getNrOfSpecials();
                nrOfSpecials.put(fieldMessage.getSender(), specials);
            }

            if (message instanceof SpecialBlockMessage) {
                SpecialBlockMessage sb = (SpecialBlockMessage) message;
                if (isClearSpecials(sb) && nrOfSpecials.getOrDefault(sb.getTarget(), 0) >= TARGET) {
                    ofNullable(allPlayers.get(sb.getSender())).ifPresent(player -> counts.compute(player, (p, x) -> x == null ? 1 : x + 1));
                }
            }
        }

        counts.entrySet().forEach(e -> {
            Player p = e.getKey();
            long nextLevel = badgeOps.getProgress(badge, p.getName()) + e.getValue();
            updateBadgeLevel(game, p.getName(), badge, nextLevel, badgeOps, onBadgeEarned);
        });
    }

    private boolean isClearSpecials(SpecialBlockMessage sb) {
        return Objects.equals(sb.getSpecial(), Special.CLEARSPECIAL);
    }

}
