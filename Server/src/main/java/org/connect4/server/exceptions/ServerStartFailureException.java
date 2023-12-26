package org.connect4.server.exceptions;

import java.io.IOException;

/**
 * An Exception which indicate a failure when starting the server.
 * @author Hassan
 */
public class ServerStartFailureException extends IOException {
    /**
     * Constructs a new ServerStartFailureException with the specified message.
     * @param message A human-readable description of the exception.
     */
    public ServerStartFailureException(String message) {
        super(message);
    }
}
