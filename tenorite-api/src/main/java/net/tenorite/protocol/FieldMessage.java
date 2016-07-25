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
public abstract class FieldMessage implements Message {

    public static FieldMessage of(int sender, String update) {
        return new FieldMessageBuilder().sender(sender).update(update).build();
    }

    public static FieldMessage of(int sender, String update, boolean serverMessage) {
        return new FieldMessageBuilder().sender(sender).update(update).serverMessage(serverMessage).build();
    }

    public abstract int getSender();

    public abstract String getUpdate();

    @Override
    @Value.Default
    public boolean isServerMessage() {
        return false;
    }

    @Override
    public String raw(Tempo tempo) {
        return String.format("f %s %s", getSender(), getUpdate());
    }

}
