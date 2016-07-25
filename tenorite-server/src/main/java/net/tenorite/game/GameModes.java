/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tenorite.game;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static java.util.stream.StreamSupport.stream;

/**
 * @author Johan Siebens
 */
public class GameModes implements Iterable<GameMode> {

    private Map<GameModeId, GameMode> modes;

    public GameModes(Iterable<GameMode> gameModes) {
        this.modes = new TreeMap<>(stream(gameModes.spliterator(), false).collect(Collectors.toMap(GameMode::getId, gameMode -> gameMode)));
    }

    public Optional<GameMode> find(GameModeId id) {
        return ofNullable(modes.get(id));
    }

    @Override
    public Iterator<GameMode> iterator() {
        return modes.values().iterator();
    }

}
