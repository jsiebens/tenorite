package net.tenorite.channel.commands;

import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Johan Siebens
 */
@Value.Immutable(singleton = true)
@ImmutableStyle
public abstract class ListChannels {

    public static ListChannels instance() {
        return ListChannelsBuilder.ImmutableListChannels.of();
    }

}
