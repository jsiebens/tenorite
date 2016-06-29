package net.tenorite.game;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

public class GameModes implements Iterable<GameMode> {

    private Map<GameModeId, GameMode> modes;

    public GameModes(List<GameMode> modes) {
        this.modes = new TreeMap<>(modes.stream().collect(Collectors.toMap(GameMode::getId, gameMode -> gameMode)));
    }

    public GameMode get(GameModeId id) {
        return ofNullable(modes.get(id)).orElseThrow(() -> new IllegalArgumentException(id.toString()));
    }

    @Override
    public Iterator<GameMode> iterator() {
        return modes.values().iterator();
    }

}
