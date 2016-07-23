package net.tenorite.clients;

import net.tenorite.clients.events.ClientRegistered;
import net.tenorite.core.Tempo;

import java.util.concurrent.CompletionStage;

/**
 * @author Johan Siebens
 */
public interface ClientsRegistry {

    CompletionStage<ClientRegistered> registerClient(Tempo tempo, String name, MessageSink sink);

}
