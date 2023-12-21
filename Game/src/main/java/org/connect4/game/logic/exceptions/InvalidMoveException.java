package org.connect4.game.logic.exceptions;

/**
 * Exception thrown to indicate that an invalid move was attempted in the Connect-4 game.
 * @author Hassan
 */
public class InvalidMoveException extends Exception {
    /**
     * Constructs a new InvalidMoveException with the specified message.
     * @param message A human-readable description of the exception.
     */
    public InvalidMoveException(String message) {
        super(message);
    }
}
