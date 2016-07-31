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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.tenorite.badges.Badge;
import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class GameModeId implements Comparable<GameModeId> {

    @JsonCreator
    public static GameModeId of(String name) {
        return new GameModeIdBuilder().value(name).build();
    }

    abstract String value();

    @Override
    public int compareTo(GameModeId o) {
        return value().compareTo(o.value());
    }

    public String getTitle(Tempo tempo) {
        Properties props = properties(this);
        return props.getProperty("title." + tempo, props.getProperty("title", value()));
    }

    public String getDescription(Tempo tempo) {
        Properties props = properties(this);
        return props.getProperty("description." + tempo, props.getProperty("description", ""));
    }

    public Optional<String> getProperty(String key) {
        return Optional.ofNullable(properties(this).getProperty(key));
    }

    @Override
    @JsonValue
    public String toString() {
        return value();
    }

    private static final LoadingCache<GameModeId, Properties> PROPERTIES = CacheBuilder.newBuilder()
        .maximumSize(10)
        .build(new CacheLoader<GameModeId, Properties>() {

            @Override
            public Properties load(GameModeId key) throws Exception {
                return loadAllProperties("/" + key + ".properties");
            }

        });

    private static Properties properties(GameModeId gameModeId) {
        return PROPERTIES.getUnchecked(gameModeId);
    }

    private static Properties loadAllProperties(String resourceName) throws IOException {
        Properties props = new Properties();
        try (InputStream is = Badge.class.getResourceAsStream(resourceName)) {
            if (is != null) {
                props.load(is);
            }
        }
        return props;
    }

}
