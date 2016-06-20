package net.tenorite.game;

import net.tenorite.protocol.PlayerLeaveMessage;
import net.tenorite.protocol.PlayerLostMessage;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public final class GameRankCalculator {

    public List<Player> calculate(Game game) {
        return new Calculator(game).process();
    }

    private static class Calculator {

        private final Game game;

        private final Map<Integer, Player> players = new LinkedHashMap<>();

        private final LinkedList<Player> ranking = new LinkedList<>();

        private Calculator(Game game) {
            this.game = game;
            this.players.putAll(game.getPlayers().stream().collect(toMap(Player::getSlot, identity())));
        }

        private List<Player> process() {
            game.getMessages().forEach(this::process);
            players.values().stream().forEach(ranking::addFirst);
            return new LinkedList<>(ranking);
        }

        private void process(GameMessage m) {
            if (m.getMessage() instanceof PlayerLostMessage) {
                process((PlayerLostMessage) m.getMessage());
            }
            else if (m.getMessage() instanceof PlayerLeaveMessage) {
                process((PlayerLeaveMessage) m.getMessage());
            }
        }

        private void process(PlayerLostMessage message) {
            ofNullable(players.remove(message.getSender())).ifPresent(ranking::addFirst);
        }

        private void process(PlayerLeaveMessage message) {
            players.remove(message.getSender());
        }

    }

}
