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

import net.tenorite.core.Special;
import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class SpecialBlockMessage implements Message {

    public static SpecialBlockMessage of(int sender, Special special, int target) {
        return new SpecialBlockMessageBuilder().sender(sender).special(special).target(target).build();
    }

    public static SpecialBlockMessage of(int sender, Special special, int target, boolean serverMessage) {
        return new SpecialBlockMessageBuilder().sender(sender).special(special).target(target).serverMessage(serverMessage).build();
    }

    public abstract int getSender();

    public abstract Special getSpecial();

    public abstract int getTarget();

    @Override
    @Value.Default
    public boolean isServerMessage() {
        return false;
    }

    @Override
    public String raw(Tempo tempo) {
        return String.format("sb %s %s %s", getTarget(), getSpecial().getLetter(), getSender());
    }

}
