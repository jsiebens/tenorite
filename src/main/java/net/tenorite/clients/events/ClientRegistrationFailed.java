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
package net.tenorite.clients.events;

import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class ClientRegistrationFailed {

    public static ClientRegistrationFailed invalidName() {
        return new ClientRegistrationFailedBuilder().type(Type.INVALID_NAME).build();
    }

    public static ClientRegistrationFailed nameAlreadyInUse() {
        return new ClientRegistrationFailedBuilder().type(Type.NAME_ALREADY_IN_USE).build();
    }

    public static ClientRegistrationFailed of(Type type) {
        return new ClientRegistrationFailedBuilder().type(type).build();
    }

    public enum Type {
        INVALID_NAME,
        NAME_ALREADY_IN_USE
    }

    public abstract Type getType();

}
