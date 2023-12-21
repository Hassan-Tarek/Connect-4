package org.connect4.game.logic.exceptions;

/**
 * Exception thrown to indicate that a column index is out of bounds in the Connect-4 game.
 * @author Hassan
 */
public class InvalidColumnIndexException extends Exception {
    /**
     * Constructs a new InvalidColumnIndexException with the specified message.
     * @param message A human-readable description of the exception.
     */
    public InvalidColumnIndexException(String message) {
        super(message);
    }
}
