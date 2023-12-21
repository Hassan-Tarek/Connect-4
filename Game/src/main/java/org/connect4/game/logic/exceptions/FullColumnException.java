package org.connect4.game.logic.exceptions;

/**
 * Exception thrown to indicate that a column in full in the Connect-4 game.
 * @author Hassan
 */
public class FullColumnException extends Exception {
    /**
     * Constructs a new FullColumnException with the specified message.
     * @param message A human-readable description of the exception.
     */
    public FullColumnException(String message) {
        super(message);
    }
}
