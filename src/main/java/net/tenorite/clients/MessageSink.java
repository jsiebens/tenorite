package net.tenorite.clients;

import net.tenorite.protocol.Message;

/**
 * @author Johan Siebens
 */
public interface MessageSink {

    void write(Message message);

    void close();

}
