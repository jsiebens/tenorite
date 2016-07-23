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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.tenorite.core.Tempo;
import net.tenorite.protocol.Message;
import net.tenorite.protocol.MessageParser;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class GameMessage {

    @JsonCreator
    public static GameMessage of(@JsonProperty("timestamp") long timestamp, @JsonProperty("message") String message, @JsonProperty("server") boolean server) {
        return of(timestamp, MessageParser.parse(message, server).orElseThrow(IllegalStateException::new));
    }

    public static GameMessage of(long timestamp, Message message) {
        return new GameMessageBuilder().timestamp(timestamp).message(message).build();
    }

    public abstract long getTimestamp();

    @JsonIgnore
    public abstract Message getMessage();

    @JsonProperty("message")
    private String message() {
        return getMessage().raw(Tempo.NORMAL);
    }

    @JsonProperty("server")
    private boolean server() {
        return getMessage().isServerMessage();
    }

}
