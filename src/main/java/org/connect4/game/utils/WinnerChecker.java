package org.connect4.game.utils;

import org.connect4.game.core.Board;
import org.connect4.game.enums.Color;
import org.connect4.logging.GameLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class represents the utility for checking the winner in a Connect-4 game.
 * @author hassan
 */
public class WinnerChecker {
    public static final Logger logger = GameLogger.getLogger();
    public static final int CONSECUTIVE_PIECES_FOR_WIN = 4;

    /**
     * Checks whether the board has a winner.
     * @param board The board to check.
     * @return true if the board has a winner, false otherwise.
     */
    public static boolean hasWinner(Board board) {
        boolean hasWinner =  checkRows(board) || checkColumns(board) || checkDiagonals(board);

        if (hasWinner) {
            logger.info("Board has a winner.");
        }

        return hasWinner;
    }

    /**
     * Checks if any row of the board has a winner.
     * @param board The board to check.
     * @return true if any row of the board has a winner, false otherwise.
     */
    private static boolean checkRows(Board board) {
        for (int i = 0; i < Board.ROWS; i++) {
            if (isRowWinner(board, i)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if any column of the board has a winner.
     * @param board The board to check.
     * @return true if any column of the board has a winner, false otherwise.
     */
    private static boolean checkColumns(Board board) {
        for (int i = 0; i < Board.COLS; i++) {
            if (isColWinner(board, i)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if any diagonal of the board has a winner.
     * @param board The board to check.
     * @return true if any diagonal of the board has a winner, false otherwise.
     */
    private static boolean checkDiagonals(Board board) {
        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLS; j++) {
                if (isDiagonalWinner(board, i, j)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if a row has a winning sequence of consecutive pieces.
     * @param board The board to check.
     * @param rowIndex The index of the row to check.
     * @return true if the row has a winning sequence, false otherwise.
     */
    private static boolean isRowWinner(Board board, int rowIndex) {
        return isLineWinner(board, rowIndex, 0, 0, 1);
    }

    /**
     * Checks if a column has a winning sequence of consecutive pieces.
     * @param board The board to check.
     * @param colIndex The index of the column to check.
     * @return true if the column has a winning sequence, false otherwise.
     */
    private static boolean isColWinner(Board board, int colIndex) {
        return isLineWinner(board, 0, colIndex, 1, 0);
    }

    /**
     * Checks if a diagonal has a winning sequence of consecutive pieces.
     * @param board The board to check.
     * @param rowIndex The starting row index of the diagonal.
     * @param colIndex The starting column index of the diagonal.
     * @return true if the diagonal has a winning sequence, false otherwise.
     */
    private static boolean isDiagonalWinner(Board board, int rowIndex, int colIndex) {
        return isDiagonalWinnerLeftToRight(board, rowIndex, colIndex) ||
                isDiagonalWinnerRightToLeft(board, rowIndex, colIndex);
    }

    /**
     * Checks if a diagonal (top-left to bottom-right) has a winning sequence of consecutive pieces.
     * @param board The board to check.
     * @param rowIndex The starting row index of the diagonal.
     * @param colIndex The starting column index of the diagonal.
     * @return true if the diagonal has a winning sequence, false otherwise.
     */
    private static boolean isDiagonalWinnerLeftToRight(Board board, int rowIndex, int colIndex) {
        return isLineWinner(board, rowIndex, colIndex, 1, 1);
    }

    /**
     * Checks if a diagonal (top-right to bottom-left) has a winning sequence of consecutive pieces.
     * @param board The board to check.
     * @param rowIndex The starting row index of the diagonal.
     * @param colIndex The starting column index of the diagonal.
     * @return true if the diagonal has a winning sequence, false otherwise.
     */
    private static boolean isDiagonalWinnerRightToLeft(Board board, int rowIndex, int colIndex) {
        return isLineWinner(board, rowIndex, colIndex, 1, -1);
    }

    /**
     * Checks whether a line has a winning sequence of consecutive pieces.
     * @param board The board to check.
     * @param rowIndex The starting row index of the line.
     * @param colIndex The starting column index of the line.
     * @param rowOffset The offset for moving along the row.
     * @param colOffset The offset for moving along the column.
     * @return true if the line has a winning sequence, false otherwise.
     */
    private static boolean isLineWinner(Board board, int rowIndex, int colIndex, int rowOffset, int colOffset) {
        Color lastColor = null;
        int count = 0;
        int currentRow = rowIndex;
        int currentCol = colIndex;

        while (currentRow >= 0 && currentRow < Board.ROWS && currentCol >= 0 && currentCol < Board.COLS) {
            if (board.getPieceAt(currentRow, currentCol) != null) {
                logger.log(Level.FINE, "Piece is not null.");

                if (board.getPieceAt(currentRow, currentCol).getColor() == lastColor) {
                    count++;

                    if (count >= CONSECUTIVE_PIECES_FOR_WIN) {
                        logger.info("Player has won the game.");
                        return true;
                    }
                } else {
                    lastColor = board.getPieceAt(currentRow, currentCol).getColor();
                    count = 1;
                }
            } else {
                lastColor = null;
                count = 0;
            }

            currentRow += rowOffset;
            currentCol += colOffset;
        }

        return false;
    }
}
