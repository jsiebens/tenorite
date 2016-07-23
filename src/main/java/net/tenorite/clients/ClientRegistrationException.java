package net.tenorite.clients;

import net.tenorite.clients.events.ClientRegistrationFailed;

/**
 * @author Johan Siebens
 */
public final class ClientRegistrationException extends RuntimeException {

    private final ClientRegistrationFailed failed;

    public ClientRegistrationException(ClientRegistrationFailed failed) {
        super(failed.getType().name());
        this.failed = failed;
    }

    public ClientRegistrationFailed.Type getType() {
        return failed.getType();
    }

}
