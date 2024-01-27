package org.connect4.client.exceptions;

import java.io.IOException;

/**
 * An Exception which indicates a failure while starting the client.
 * @author Hassan
 */
public class ServerConnectionFailureException extends IOException {
    /**
     * Constructs a new ServerStartFailureException with the specified message.
     * @param message A human-readable description of the exception.
     */
    public ServerConnectionFailureException(String message) {
        super(message);
    }
}
