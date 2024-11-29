package org.connect4.game.logic.utils;

import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logging.GameLogger;

import java.util.logging.Logger;

/**
 * A class represents the utility for checking the winner in a Connect-4 game.
 * @author hassan
 */
public class WinnerChecker {
    public static final Logger LOGGER = GameLogger.getLogger();
    public static final int CONSECUTIVE_PIECES_FOR_WIN = 4;

    private final Board board;

    /**
     * Constructs a WinnerChecker for the given board.
     * @param board The game board to check for a winner.
     */
    public WinnerChecker(Board board) {
        this.board = board;
    }

    /**
     * Checks whether the board has a winner.
     * @return true if the board has a winner, false otherwise.
     */
    public boolean hasWinner() {
        Color winnerColor = determineWinner();
        boolean hasWinner = winnerColor != Color.NONE;

        if (hasWinner) {
            LOGGER.info("Board has a winner.");
        }

        return hasWinner;
    }

    /**
     * Determines the winner if there is one.
     * @return The winning player's color, or Color.NONE if there is no winner.
     */
    public Color determineWinner() {
        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLS; j++) {
                if (board.getPieceAt(i, j) != null) {
                    if (checkDirection(i, 0, 0, 1) || checkDirection(0, j, 1, 0)
                            || checkDirection(i, j, 1, 1) || checkDirection(i, j, 1, -1)) {
                        LOGGER.info("Winner is determined at row: " + i + " and col: " + j);
                        return board.getPieceAt(i, j).getColor();
                    }
                }
            }
        }

        return Color.NONE;
    }

    /**
     * Determines the winner of the game if it has one.
     * @param rowIndex The starting row index.
     * @param colIndex The starting column index.
     * @param rowOffset The row offset for iterating through consecutive pieces.
     * @param colOffset The column offset for iterating through consecutive pieces.
     * @return The color of winning player or `null` if there is no winner.
     */
    private boolean checkDirection(int rowIndex, int colIndex, int rowOffset, int colOffset) {
        Color color = board.getPieceAt(rowIndex, colIndex).getColor();

        for (int k = 0; k < WinnerChecker.CONSECUTIVE_PIECES_FOR_WIN; k++) {
            int x = rowIndex + rowOffset * k;
            int y = colIndex + colOffset * k;

            if (!board.isInBound(x, y) || board.getPieceAt(x, y) == null
                    || board.getPieceAt(x, y).getColor() != color) {
                return false;
            }
        }

        LOGGER.info("Winner determined: " + color + " Player.");
        return true;
    }
}
