package org.connect4.ai.heuristics;

import org.connect4.game.core.Board;
import org.connect4.game.core.Piece;
import org.connect4.game.enums.Color;
import org.connect4.logging.AILogger;

import java.util.logging.Logger;

/**
 * A Class evaluates the heuristic scores for Connect-4 game states.
 * @author hassan
 */
public class Heuristic {
    private static final Logger logger = AILogger.getLogger();

    private static final int WIN_SCORE = 1000;
    private static final int LOSE_SCORE = -1000;
    private static final int THREE_IN_ROW_SCORE = 100;
    private static final int TWO_IN_ROW_SCORE = 10;
    private static final int ONE_IN_ROW_SCORE = 1;
    private static final int CENTER_COLUMN_SCORE = 50;
    private static final int DEFAULT_SCORE = 0;

    /**
     * Evaluates the heuristic score for the given game board.
     * @param board The game board to evaluate.
     * @return The heuristic score for right diagonals.
     */
    public static int evaluate(Board board) {
        int totalScore = 0;

        // Evaluate scores for rows, columns, diagonals
        totalScore += evaluateRows(board);
        totalScore += evaluateColumns(board);
        totalScore += evaluateDiagonals(board);
        totalScore += evaluateCenterColumnScore(board);

        return totalScore;
    }

    /**
     * Evaluates the heuristic score for all rows in the game board.
     * @param board The game board to evaluate.
     * @return The heuristic score for right diagonals.
     */
    private static int evaluateRows(Board board) {
        int totalScore = 0;
        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col <= Board.COLS - 4; col++) {
                logger.finest("Evaluate score of row: " + row);
                totalScore += evaluateLineScore(board, row, col, 0, 1);
            }
        }
        return totalScore;
    }

    /**
     * Evaluates the heuristic score for all columns in the game board.
     * @param board The game board to evaluate.
     * @return The heuristic score for right diagonals.
     */
    private static int evaluateColumns(Board board) {
        int totalScore = 0;
        for (int row = 0; row <= Board.ROWS; row++) {
            for (int col = 0; col < Board.COLS; col++) {
                logger.finest("Evaluate score of column: " + col);
                totalScore += evaluateLineScore(board, row, col, 1, 0);
            }
        }
        return totalScore;
    }

    /**
     * Evaluates the heuristic score for all diagonals in the game board.
     * @param board The game board to evaluate.
     * @return The heuristic score for right diagonals.
     */
    private static int evaluateDiagonals(Board board) {
        int totalScore = 0;
        // Evaluate diagonals starting from top-left corner
        totalScore += evaluateLeftDiagonalsScore(board);

        // Evaluate diagonals starting from top-right corner
        totalScore += evaluateRightDiagonalsScore(board);

        return totalScore;
    }

    /**
     * Evaluates the total heuristic score of diagonals from top-left to bottom-rights in the game board.
     * @param board The game board to evaluate.
     * @return The heuristic score for right diagonals.
     */
    private static int evaluateLeftDiagonalsScore(Board board) {
        int totalScore = 0;

        for (int row = 0; row <= Board.ROWS - 4; row++) {
            for (int col = 0; col <= Board.COLS - 4; col++) {
                logger.finest("Evaluate score of left diagonal at row: " + row + " and col: " + col);
                totalScore += evaluateLineScore(board, row, col, 1, 1);
            }
        }

        return totalScore;
    }

    /**
     * Evaluates the total heuristic score of diagonals form top-right to bottom-left in the game board.
     * @param board The game board to evaluate.
     * @return The heuristic score for right diagonals.
     */
    private static int evaluateRightDiagonalsScore(Board board) {
        int totalScore = 0;

        for (int row = 0; row <= Board.ROWS - 4; row++) {
            for (int col = Board.COLS - 1; col >= 3; col--) {
                logger.finest("Evaluate score of right diagonal at row: " + row + " and col: " + col);
                totalScore += evaluateLineScore(board, row, col, 1, -1);
            }
        }

        return totalScore;
    }

    /**
     * Evaluates the heuristic score for a line of pieces on the game board.
     * @param board The game board to evaluate.
     * @param rowIndex The index of the row where the line starts.
     * @param colIndex The index of the column where the line starts.
     * @param rowOffset The offset for moving along rows.
     * @param colOffset The offset for moving along columns.
     * @return The heuristic score for the specified line.
     */
    private static int evaluateLineScore(Board board, int rowIndex, int colIndex, int rowOffset, int colOffset) {
        int aiPiecesCount = 0;
        int humanPiecesCount = 0;
        int emptySpacesCount = 0;

        for (int i = 0; i < 4; i++) {
            int row = rowIndex + rowOffset * i;
            int col = colIndex + colOffset * i;
            Piece piece = board.getPieceAt(row, col);

            if (piece != null) {
                if (piece.getColor() == Color.RED) {
                    aiPiecesCount++;
                } else {
                    humanPiecesCount++;
                }
            } else {
                emptySpacesCount++;
            }
        }

        return evaluateScore(aiPiecesCount, humanPiecesCount, emptySpacesCount);
    }

    /**
     * Evaluates the heuristic score for the center column of the game board.
     * @param board The game board to evaluate.
     * @return The heuristic score for the center column.
     */
    private static int evaluateCenterColumnScore(Board board) {
        int totalScore = 0;

        for (int row = 0; row < Board.ROWS; row++) {
            Piece piece = board.getPieceAt(row, Board.COLS / 2);
            if (piece != null && piece.getColor() == Color.RED) {
                logger.finest("AI piece found in the center column at row: " + row);
                totalScore += CENTER_COLUMN_SCORE;
            }
        }

        return totalScore;
    }

    /**
     * Evaluates the heuristic score based on the number of pieces in a line and number of empty spaces.
     * @param aiPiecesCount The number of pieces belonging to the AI player in the line.
     * @param humanPiecesCount The number of pieces belonging to the human player in the line.
     * @param emptySpacesCount The number of empty spaces in the line.
     * @return The heuristic score for the line.
     */
    private static int evaluateScore(int aiPiecesCount, int humanPiecesCount, int emptySpacesCount) {
        int score = DEFAULT_SCORE;

        if (aiPiecesCount == 4) {
            score = WIN_SCORE;
        } else if (humanPiecesCount == 4) {
            score = LOSE_SCORE;
        } else if (aiPiecesCount == 3 && emptySpacesCount >= 1) {
            score = THREE_IN_ROW_SCORE;
        } else if (humanPiecesCount == 3 && emptySpacesCount >= 1) {
            score = -1 * THREE_IN_ROW_SCORE;
        } else if (aiPiecesCount == 2 && emptySpacesCount >= 2) {
            score = TWO_IN_ROW_SCORE;
        } else if (humanPiecesCount == 2 && emptySpacesCount >= 2) {
            score = -1 * TWO_IN_ROW_SCORE;
        } else if (aiPiecesCount == 1 && emptySpacesCount >= 3) {
            score = ONE_IN_ROW_SCORE;
        } else if (humanPiecesCount == 1 && emptySpacesCount >= 3) {
            score = -1 * ONE_IN_ROW_SCORE;
        }

        logger.finest("Evaluated score: " + score);
        return score;
    }
}
