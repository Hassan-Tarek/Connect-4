package org.connect4.ai.heuristics;

import org.connect4.ai.utils.State;
import org.connect4.game.core.Board;
import org.connect4.game.core.Piece;
import org.connect4.game.enums.Color;

import java.util.function.BinaryOperator;

public class Heuristic {
    private static final int WIN_SCORE = 1000;
    private static final int LOSE_SCORE = -1000;
    private static final int THREE_IN_ROW_SCORE = 100;
    private static final int TWO_IN_ROW_SCORE = 10;
    private static final int ONE_IN_ROW_SCORE = 1;
    private static final int DEFAULT_SCORE = 0;

    public static int evaluate(State state) {
        int maxScore;
        BinaryOperator<Integer> operator = state.getPlayerColor() == Color.RED ? Math::max : Math::min;

        // Evaluate scores for rows, columns, diagonals
        int maxRowScore = evaluateRows(state, operator);
        int maxColumnScore = evaluateColumns(state, operator);
        int maxDiagonalScore = evaluateDiagonals(state, operator);

        maxScore = operator.apply(operator.apply(maxRowScore, maxColumnScore), maxDiagonalScore);
        return maxScore;
    }

    private static int evaluateRows(State state, BinaryOperator<Integer> operator) {
        int bestScore = 0;
        for (int row = 0; row < Board.ROWS; row++) {
            int score = evaluateRowScore(state, row);
            bestScore = operator.apply(bestScore, score);
        }
        return bestScore;
    }

    private static int evaluateRowScore(State state, int rowIndex) {
        int maxPiecesCount = 0;
        boolean isValidRow = false;

        for (int i = 0; i < Board.COLS - 4; i++) {
            int count = 0;
            boolean isOfTheSameColor = true;

            for (int j = 0; j < 4; j++) {
                Piece piece = state.getBoard().getPieceAt(rowIndex, i + j);

                if (piece != null) {
                    if (piece.getColor() == state.getPlayerColor()) {
                        count++;
                    }
                    else {
                        isOfTheSameColor = false;
                        break;
                    }
                }
            }

            if (isOfTheSameColor) {
                isValidRow = true;
                maxPiecesCount = Math.max(maxPiecesCount, count);
            }
        }

        return evaluateScore(state, maxPiecesCount, isValidRow);
    }

    private static int evaluateColumns(State state, BinaryOperator<Integer> operator) {
        int bestScore = 0;
        for (int col = 0; col < Board.COLS; col++) {
            int score = evaluateColumnScore(state, col);
            bestScore = operator.apply(bestScore, score);
        }
        return bestScore;
    }

    private static int evaluateColumnScore(State state, int colIndex) {
        int maxPiecesCount = 0;
        boolean isValidCol = false;

        for (int i = 0; i < Board.ROWS - 4; i++) {
            int count = 0;
            boolean isOfTheSameColor = true;

            for (int j = 0; j < 4; j++) {
                Piece piece = state.getBoard().getPieceAt(i + j, colIndex);

                if (piece != null) {
                    if (piece.getColor() == state.getPlayerColor()) {
                        count++;
                    }
                    else {
                        isOfTheSameColor = false;
                        break;
                    }
                }
            }

            if (isOfTheSameColor) {
                isValidCol = true;
                maxPiecesCount = Math.max(maxPiecesCount, count);
            }
        }

        return evaluateScore(state, maxPiecesCount, isValidCol);
    }

    private static int evaluateDiagonals(State state, BinaryOperator<Integer> operator) {
        // Evaluate diagonals starting from top-left corner
        int bestLeftDiagonalsScore = evaluateLeftDiagonalsScore(state, operator);

        // Evaluate diagonals starting from top-right corner
        int bestRightDiagonalsScore = evaluateRightDiagonalsScore(state, operator);

        return operator.apply(bestLeftDiagonalsScore, bestRightDiagonalsScore);
    }

    private static int evaluateLeftDiagonalsScore(State state, BinaryOperator<Integer> operator) {
        int bestScore = 0;

        for (int row = 0; row <= Board.ROWS - 4; row++) {
            for (int col = 0; col <= Board.COLS - 4; col++) {
                int score = evaluateDiagonalScore(state, row, col, true);
                bestScore = operator.apply(bestScore, score);
            }
        }

        return bestScore;
    }

    private static int evaluateRightDiagonalsScore(State state, BinaryOperator<Integer> operator) {
        int bestScore = 0;

        for (int row = 0; row <= Board.ROWS - 4; row++) {
            for (int col = Board.COLS - 1; col >= 3; col--) {
                int score = evaluateDiagonalScore(state, row, col, false);
                bestScore = operator.apply(bestScore, score);
            }
        }

        return bestScore;
    }

    private static int evaluateDiagonalScore(State state, int rowIndex, int colIndex, boolean isLeftDiagonal) {
        int offset = isLeftDiagonal ? 1 : -1;
        int maxPiecesCount = 0;
        boolean isValidCol = false;

        for (int i = rowIndex; i < Board.ROWS - 4; i++) {
            int j = colIndex;
            while (state.getBoard().isValidMove(j + 4 * offset)) {
                int count = 0;
                boolean isOfTheSameColor = true;

                for (int k = 0; k < 4; k++) {
                    int row = i + k;
                    int col = j + k * offset;
                    Piece piece = state.getBoard().getPieceAt(row, col);

                    if (piece != null) {
                        if (piece.getColor() == state.getPlayerColor()) {
                            count++;
                        } else {
                            isOfTheSameColor = false;
                            break;
                        }
                    }
                }

                if (isOfTheSameColor) {
                    isValidCol = true;
                    maxPiecesCount = Math.max(maxPiecesCount, count);
                }

                j += offset;
            }
        }

        return evaluateScore(state, maxPiecesCount, isValidCol);
    }

    private static int evaluateScore(State state, int piecesCount, boolean isValidLine) {
        int offset = state.getPlayerColor() == Color.RED ? 1 : -1;
        int score = DEFAULT_SCORE;

        if (piecesCount == 4 && state.getPlayerColor() == Color.RED) {
            score = WIN_SCORE;
        } else if (piecesCount == 4 && state.getPlayerColor() == Color.YELLOW) {
            score = LOSE_SCORE;
        } else if (piecesCount == 3 && isValidLine) {
            score = offset * THREE_IN_ROW_SCORE;
        } else if (piecesCount == 2 && isValidLine) {
            score = offset * TWO_IN_ROW_SCORE;
        } else if (piecesCount == 1 && isValidLine) {
            score = offset * ONE_IN_ROW_SCORE;
        }

        return score;
    }
}
