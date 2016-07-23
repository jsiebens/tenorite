package net.tenorite.protocol;

import net.tenorite.core.Tempo;
import net.tenorite.util.ImmutableStyle;
import org.immutables.value.Value;

import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * @author Johan Siebens
 */
@Value.Immutable
@ImmutableStyle
public abstract class WinlistMessage implements Message {

    public static WinlistMessage of(List<String> winlist) {
        return new WinlistMessageBuilder().winlist(winlist).build();
    }

    public abstract List<String> getWinlist();

    @Override
    public String raw(Tempo tempo) {
        return "winlist " + getWinlist().stream().collect(joining(" "));
    }

}
