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

import com.google.common.base.Preconditions;
import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class ClassicStyleAddMessage implements Message {

    public static ClassicStyleAddMessage of(int sender, int lines) {
        return new ClassicStyleAddMessageBuilder().sender(sender).lines(lines).build();
    }

    public abstract int getSender();

    public abstract int getLines();

    @Value.Check
    protected void check() {
        int lines = getLines();
        Preconditions.checkState((lines == 1 || lines == 2 || lines == 4), "invalid value " + lines + " for 'lines', allowed values are 1, 2 or 4");
    }

    @Override
    public String raw(Tempo tempo) {
        return String.format("sb 0 cs%s %s", getLines(), getSender());
    }

}
