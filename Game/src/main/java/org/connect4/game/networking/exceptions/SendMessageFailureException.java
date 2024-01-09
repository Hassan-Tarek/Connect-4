package org.connect4.game.networking.exceptions;

import java.io.IOException;

/**
 * Exception thrown to indicate that a failure occurred while sending a message.
 * @author Hassan
 */
public class SendMessageFailureException extends IOException {
    /**
     * Constructs a new SendMessageFailureException with the specified message.
     * @param message A human-readable description of the exception.
     */
    public SendMessageFailureException(String message) {
        super(message);
    }
}
