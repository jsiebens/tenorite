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
package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class PlineMessage implements Message {

    public static PlineMessage of(String message) {
        return PlineMessage.of(0, message);
    }

    public static PlineMessage of(int sender, String message) {
        return new PlineMessageBuilder().sender(sender).message(message).build();
    }

    public abstract int getSender();

    public abstract String getMessage();

    @Override
    public String raw(Tempo tempo) {
        return String.format("pline %s %s", getSender(), getMessage());
    }

}
