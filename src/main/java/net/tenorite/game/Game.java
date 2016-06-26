package net.tenorite.game;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@ImmutableStyle
public abstract class Game {

    public static Game of(String id,
                          long timestamp,
                          long duration,
                          Tempo tempo,
                          GameMode gameMode,
                          List<Player> players,
                          List<GameMessage> messages) {
        return
            new GameBuilder()
                .id(id)
                .duration(duration)
                .timestamp(timestamp)
                .tempo(tempo)
                .gameMode(gameMode)
                .players(players)
                .messages(messages)
                .build();
    }

    public abstract String getId();

    public abstract Tempo getTempo();

    public abstract GameMode getGameMode();

    public abstract long getTimestamp();

    public abstract long getDuration();

    public abstract List<Player> getPlayers();

    public abstract List<GameMessage> getMessages();

    @Value.Lazy
    public GameRules getGameRules() {
        return getGameMode().getGameRules();
    }

}
