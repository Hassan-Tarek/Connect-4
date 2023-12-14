package org.connect4.game.core;

import org.connect4.game.exceptions.FullColumnException;
import org.connect4.game.exceptions.InvalidColumnIndexException;
import org.connect4.game.exceptions.InvalidMoveException;
import org.connect4.logging.GameLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class represents the game move.
 * @author Hassan
 */
public class Move {
    private static final Logger logger = GameLogger.getLogger();

    private final int column;
    private final Board board;

    /**
     * Constructs a move with the specified board and column index.
     * @param board The board on which the move is made.
     * @param column The column index where the move is made.
     */
    public Move(Board board, int column) {
        this.board = board;
        this.column = column;
    }

    /**
     * Applies the move on the board.
     * @param player The player who will make the move.
     * @throws InvalidMoveException if the move is not valid.
     */
    public void applyMove(Player player) throws InvalidMoveException {
        if (!isValid()) {
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
     * Gets the board on which the move is made.
     * @return The board.
     */
    public Board getBoard() {
        return board;
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
     * @return true if the move is valid, false otherwise.
     */
    public boolean isValid() {
        boolean isValid = board.isValidColumn(column) && !board.isColumnFull(column);

        if (isValid) {
            logger.log(Level.FINE, "Move is valid: Column: {0}", column);
        } else {
            logger.log(Level.WARNING, "Move is not valid: Column: {0}", column);
        }

        return isValid;
    }
}
