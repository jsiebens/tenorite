package net.tenorite.clients;

import net.tenorite.protocol.Message;

public interface MessageSink {

    void write(Message message);

    void close();

}
