package net.tenorite.channel.commands;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public abstract class JoinChannel {

    public abstract Tempo getTempo();

    public abstract String getChannel();

    public abstract String getName();

}
