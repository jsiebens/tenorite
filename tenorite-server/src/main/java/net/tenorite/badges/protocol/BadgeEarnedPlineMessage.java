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
package net.tenorite.badges.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.protocol.Message;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

import static java.lang.String.format;

/**
 * @author Johan Siebens
 */
@ImmutableStyle
@Value.Immutable
public abstract class BadgeEarnedPlineMessage implements Message {

    public static BadgeEarnedPlineMessage of(String name, String title, long level, boolean upgrade) {
        return new BadgeEarnedPlineMessageBuilder().name(name).title(title).level(level).upgrade(upgrade).build();
    }

    public abstract String getName();

    public abstract String getTitle();

    public abstract long getLevel();

    public abstract boolean isUpgrade();

    @Override
    public String raw(Tempo tempo) {
        return isUpgrade() ?
            format("pline 0 <brown><b>%s</b> upgraded a badge <b>%s</b> - Level %s</brown>", getName(), getTitle(), getLevel()) :
            format("pline 0 <brown><b>%s</b> earned a badge <b>%s</b> - Level %s</brown>", getName(), getTitle(), getLevel());
    }

}
