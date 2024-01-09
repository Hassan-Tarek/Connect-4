package org.connect4.game.networking.exceptions;

import java.io.IOException;

/**
 * Exception throw to indicate that a failure occurred while receiving a message.
 * @author Hassan
 */
public class ReceiveMessageFailureException extends IOException {
    /**
     * Constructs a new ReceiveMessageFailureException with the specified message.
     * @param message A human-readable description of the exception.
     */
    public ReceiveMessageFailureException(String message) {
        super(message);
    }
}
