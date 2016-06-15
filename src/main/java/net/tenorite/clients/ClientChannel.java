package net.tenorite.clients;

import net.tenorite.protocol.Message;

public interface ClientChannel {

    void write(Message message);

    void close();

}
