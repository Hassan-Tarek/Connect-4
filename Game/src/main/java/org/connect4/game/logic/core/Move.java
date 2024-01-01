package org.connect4.game.logic.core;

import org.connect4.game.logic.exceptions.FullColumnException;
import org.connect4.game.logic.exceptions.InvalidColumnIndexException;
import org.connect4.game.logic.exceptions.InvalidMoveException;
import org.connect4.game.logging.GameLogger;

import java.io.Serial;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class represents the game move.
 * @author Hassan
 */
public class Move implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger logger = GameLogger.getLogger();

    private final int column;

    /**
     * Constructs a move with the specified board and column index.
     * @param column The column index where the move is made.
     */
    public Move(int column) {
        this.column = column;
    }

    /**
     * Applies the move on the board.
     * @param player The player who will make the move.
     * @param board The board in which the move will be performed.
     * @throws InvalidMoveException if the move is not valid.
     */
    public void applyMove(Player player, Board board) throws InvalidMoveException {
        if (!isValid(board)) {
            throw new InvalidMoveException("Invalid move.");
        }

        try {
            board.addPiece(column, player.getColor());
        } catch (InvalidColumnIndexException | FullColumnException e) {
            logger.log(Level.SEVERE, "Invalid move: " + e.getMessage());
            throw new InvalidMoveException("Invalid move: " + e.getMessage());
        }
    }

    /**
     * Gets the column index of the move.
     * @return The column index.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Check whether a move is valid or not.
     * @param board The board.
     * @return true if the move is valid, false otherwise.
     */
    public boolean isValid(Board board) {
        boolean isValid = board.isValidColumn(column) && !board.isColumnFull(column);

        if (isValid) {
            logger.log(Level.FINE, "Move is valid: Column: {0}", column);
        } else {
            logger.log(Level.WARNING, "Move is not valid: Column: {0}", column);
        }

        return isValid;
    }
}
