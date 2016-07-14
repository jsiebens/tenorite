package net.tenorite.protocol;

import net.tenorite.core.Tempo;

public interface Message {

    String raw(Tempo tempo);

    default boolean isServerMessage() {
        return false;
    }

}
