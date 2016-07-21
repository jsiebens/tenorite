package net.tenorite.game;

import net.tenorite.core.NotAvailableException;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

public class GameModes implements Iterable<GameMode> {

    private Map<GameModeId, GameMode> modes;

    public GameModes(List<GameMode> modes) {
        this.modes = new TreeMap<>(modes.stream().collect(Collectors.toMap(GameMode::getId, gameMode -> gameMode)));
    }

    public Optional<GameMode> find(GameModeId id) {
        return ofNullable(modes.get(id));
    }

    @Deprecated
    public GameMode get(GameModeId id) {
        return ofNullable(modes.get(id)).orElseThrow(NotAvailableException::new);
    }

    @Override
    public Iterator<GameMode> iterator() {
        return modes.values().iterator();
    }

}
