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
package net.tenorite.channel.events;

/**
 * @author Johan Siebens
 */
public enum SlotReservationFailed {

    CHANNEL_IS_FULL,
    CHANNEL_NOT_AVAILABLE,
    MATCH_ALREADY_FINISHED,
    MATCH_STILL_BLOCKED;

    public static SlotReservationFailed channelIsFull() {
        return CHANNEL_IS_FULL;
    }

    public static SlotReservationFailed channelNotAvailable() {
        return CHANNEL_NOT_AVAILABLE;
    }

    public static SlotReservationFailed matchAlreadyFinished() {
        return MATCH_ALREADY_FINISHED;
    }

    public static SlotReservationFailed matchStillBlocked() {
        return MATCH_STILL_BLOCKED;
    }

}
