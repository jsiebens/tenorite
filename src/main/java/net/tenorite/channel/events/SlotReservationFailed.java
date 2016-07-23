package net.tenorite.channel.events;

/**
 * @author Johan Siebens
 */
public enum SlotReservationFailed {

    CHANNEL_IS_FULL,
    CHANNEL_NOT_AVAILABLE;

    public static SlotReservationFailed channelIsFull() {
        return CHANNEL_IS_FULL;
    }

    public static SlotReservationFailed channelNotAvailable() {
        return CHANNEL_NOT_AVAILABLE;
    }

}
