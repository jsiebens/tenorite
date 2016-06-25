package net.tenorite.game;

import net.tenorite.protocol.LvlMessage;
import net.tenorite.protocol.PlayerLeaveMessage;
import net.tenorite.protocol.PlayerLostMessage;

import java.util.*;

import static java.lang.Math.max;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public final class GameRankCalculator {

    public List<PlayingStats> calculate(Game game) {
        return new Calculator(game).process();
    }

    private static class Calculator {

        private final Game game;

        private final LinkedList<PlayingStats> ranking = new LinkedList<>();

        private final Map<Integer, Player> players = new LinkedHashMap<>();

        private final Map<Integer, Long> playingTimes = new HashMap<>();

        private final Map<Integer, Integer> levels = new HashMap<>();

        private Calculator(Game game) {
            this.game = game;
            this.players.putAll(game.getPlayers().stream().collect(toMap(Player::getSlot, identity())));

            this.levels.putAll(game.getPlayers().stream().collect(toMap(Player::getSlot, p -> 0)));
        }

        private List<PlayingStats> process() {
            game.getMessages().forEach(this::process);
            players.values().stream().map(this::stats).forEach(ranking::addFirst);
            return new LinkedList<>(ranking);
        }

        private void process(GameMessage m) {
            if (m.getMessage() instanceof PlayerLostMessage) {
                process(m.getTimestamp(), (PlayerLostMessage) m.getMessage());
            }
            else if (m.getMessage() instanceof PlayerLeaveMessage) {
                process((PlayerLeaveMessage) m.getMessage());
            }
            else if (m.getMessage() instanceof LvlMessage) {
                process((LvlMessage) m.getMessage());
            }
        }

        private void process(long timestamp, PlayerLostMessage message) {
            ofNullable(players.remove(message.getSender())).ifPresent(loser -> {
                playingTimes.put(loser.getSlot(), timestamp);
                ranking.addFirst(stats(loser));
            });
        }

        private void process(LvlMessage message) {
            levels.put(message.getSender(), message.getLevel());
        }

        private void process(PlayerLeaveMessage message) {
            players.remove(message.getSender());
        }

        private PlayingStats stats(Player player) {
            GameRules gameRules = game.getGameMode().getGameRules();
            int slot = player.getSlot();

            return PlayingStats.of(
                player,
                playingTimes.getOrDefault(slot, game.getDuration()),
                levels.get(slot),
                max(0, levels.get(slot) - gameRules.getStartingLevel()) * gameRules.getLinesPerLevel()
            );
        }

    }

}
