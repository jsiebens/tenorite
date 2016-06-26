package net.tenorite.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = GameBuilder.ImmutableGame.class)
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

    @JsonProperty("_id")
    public abstract String getId();

    public abstract Tempo getTempo();

    public abstract GameMode getGameMode();

    public abstract long getTimestamp();

    public abstract long getDuration();

    public abstract List<Player> getPlayers();

    public abstract List<GameMessage> getMessages();

    @Value.Lazy
    @JsonIgnore
    public GameRules getGameRules() {
        return getGameMode().getGameRules();
    }

}
