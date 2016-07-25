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
package net.tenorite.winlist;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = WinlistItemBuilder.ImmutableWinlistItem.class)
public abstract class WinlistItem {

    public static WinlistItem of(Type type, String name, long score, long timestamp) {
        return new WinlistItemBuilder().type(type).name(name).score(score).timestamp(timestamp).build();
    }

    public enum Type {

        PLAYER('p'),

        TEAM('t');

        private char letter;

        Type(char letter) {
            this.letter = letter;
        }

        public char getLetter() {
            return letter;
        }

    }

    public abstract Type getType();

    public abstract String getName();

    public abstract long getScore();

    public abstract long getTimestamp();

}
