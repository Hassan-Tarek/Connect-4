package org.connect4.game;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

/**
 * A class represents Connect-4 game.
 * @author Hassan
 */
public class Game {
    public static final Logger logger = Logger.getLogger(Game.class.getName());
    private static final int CONSECUTIVE_PIECES_FOR_WIN = 4;

    private final Board board;
    private final Player redPlayer;
    private final Player yellowPlayer;

    // Static block for setting up logging handlers
    static {
        try {
            FileHandler fileHandler = new FileHandler("game.log");
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.INFO);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            consoleHandler.setLevel(Level.INFO);

            logger.addHandler(fileHandler);
            logger.addHandler(consoleHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructs a new game.
     * @param board The board of the game.
     * @param redPlayer The player with red pieces.
     * @param yellowPlayer The player with yellow pieces.
     */
    public Game(Board board, Player redPlayer, Player yellowPlayer) {
        this.board = board;
        this.redPlayer = redPlayer;
        this.yellowPlayer = yellowPlayer;
    }

    /**
     * Gets the game board.
     * @return The game board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the player with red pieces.
     * @return The player with red pieces.
     */
    public Player getRedPlayer() {
        return redPlayer;
    }

    /**
     * Gets the player with yellow pieces.
     * @return The player with yellow pieces.
     */
    public Player getYellowPlayer() {
        return yellowPlayer;
    }

    /**
     * Checks whether the game has winner or not.
     * @return true if the game has a winner, false otherwise.
     */
    public boolean hasWinner() {
        boolean hasWinner = false;
        for (int i = 0; i < Board.ROWS; i++) {
            if (isRowWinner(i)) {
                hasWinner = true;
            }
        }

        for (int i = 0; i < Board.COLS; i++) {
            if (isColWinner(i)) {
                hasWinner = true;
            }
        }

        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLS; j++) {
                if (isDiagonalWinner(i, j)) {
                    hasWinner = true;
                }
            }
        }

        if (hasWinner) {
            logger.info("Game has winner.");
        }

        return hasWinner;
    }

    /**
     * Checks if the raw has 4 consecutive pieces with the same color.
     * @param rowIndex The index of the row to be checked.
     * @return true if the row has 4 consecutive pieces with the same color, false otherwise.
     */
    private boolean isRowWinner(int rowIndex) {
        return isLineWinner(rowIndex, 0, 0, 1);
    }

    /**
     * Checks if the column has 4 consecutive pieces with the same color.
     * @param colIndex The index of the column to be checked.
     * @return true if the column has 4 consecutive pieces with the same color, false otherwise.
     */
    private boolean isColWinner(int colIndex) {
        return isLineWinner(0, colIndex, 1, 0);
    }

    /**
     * Checks whether the diagonal has 4 consecutive pieces with the same color.
     * @param rowIndex The index of the row to be checked.
     * @param colIndex The index of the column to be checked.
     * @return true if the diagonal has 4 consecutive pieces with the same color, false otherwise.
     */
    private boolean isDiagonalWinner(int rowIndex, int colIndex) {
        return isDiagonalWinnerLeftToRight(rowIndex, colIndex) ||
                isDiagonalWinnerRightToLeft(rowIndex, colIndex);
    }

    /**
     * Checks if the diagonal from top-left to bottom-right has 4 consecutive pieces with the same color.
     * @param rowIndex The index of the row to be checked.
     * @param colIndex The index of the column to be checked.
     * @return true if the diagonal has 4 consecutive pieces with the same color, false otherwise.
     */
    private boolean isDiagonalWinnerLeftToRight(int rowIndex, int colIndex) {
        return isLineWinner(rowIndex, colIndex, 1, 1);
    }

    /**
     * Checks if the diagonal from top-right to bottom-left has 4 consecutive pieces with the same color.
     * @param rowIndex The index of the row to be checked.
     * @param colIndex The index of the column to be checked.
     * @return true if the diagonal has 4 consecutive pieces with the same color, false otherwise.
     */
    private boolean isDiagonalWinnerRightToLeft(int rowIndex, int colIndex) {
        return isLineWinner(rowIndex, colIndex, 1, -1);
    }

    /**
     * Checks whether a line has 4 consecutive pieces with the same color.
     * @param rowIndex The starting index of the row.
     * @param colIndex The starting index of the column.
     * @param rowOffset The offset for moving along the row.
     * @param colOffset The offset for moving along the column.
     * @return true if the line has 4 consecutive pieces with the same color, false otherwise.
     */
    private boolean isLineWinner(int rowIndex, int colIndex, int rowOffset, int colOffset) {
        Color lastColor = null;
        int count = 0;
        int currentRow = rowIndex;
        int currentCol = colIndex;

        while (currentRow >= 0 && currentRow < Board.ROWS && currentCol >= 0 && currentCol < Board.COLS) {
            if (board.getPieces()[currentRow][currentCol] != null) {
                logger.log(Level.FINE, "Piece is not null.");

                if (board.getPieces()[currentRow][currentCol].getColor() == lastColor) {
                    count++;

                    if (count >= CONSECUTIVE_PIECES_FOR_WIN) {
                        logger.info("Player has won the game.");
                        return true;
                    }
                } else {
                    lastColor = board.getPieces()[currentRow][currentCol].getColor();
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


    /**
     * Gets the winner of the game if it has one.
     * @return The winner of the game or `null` if there is no winner yet.
     */
    public Player getWinner() {
        for (int i = 0; i < Board.ROWS; i++) {
            if (isRowWinner(i)) {
                logger.info("Row winner is determined at row: " + i);
                return determineWinner(i, 0, 0, 1);
            }
        }

        for (int i = 0; i < Board.COLS; i++) {
            if (isColWinner(i)) {
                logger.info("Column winner is determined at col: " + i);
                return determineWinner(0, i, 1, 0);
            }
        }

        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLS; j++) {
                if (isDiagonalWinner(i, j)) {
                    logger.info("Diagonal winner is determined at row: " + i + " and col: " + j);

                    if (isDiagonalWinnerLeftToRight(i, j)) {
                        return determineWinner(i, j, 1, 1);
                    } else if (isDiagonalWinnerRightToLeft(i, j)) {
                        return determineWinner(i, j, 1, -1);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Determines the winner of the game if it has one.
     * @param rowIndex The starting row index.
     * @param colIndex The starting column index.
     * @param rowOffset The row offset for iterating through consecutive pieces.
     * @param colOffset The column offset for iterating through consecutive pieces.
     * @return The winning player or `null` if there is no winner.
     */
    private Player determineWinner(int rowIndex, int colIndex, int rowOffset, int colOffset) {
        int currentRow = rowIndex;
        int currentCol = colIndex;

        while (currentRow >= 0 && currentRow + CONSECUTIVE_PIECES_FOR_WIN < Board.ROWS
                && currentCol >= 0 && currentCol + CONSECUTIVE_PIECES_FOR_WIN < Board.COLS) {
            Color color = board.getPieces()[currentRow][currentCol].getColor();
            boolean isWinner = true;

            for (int k = 0; k < CONSECUTIVE_PIECES_FOR_WIN; k++) {
                int x = currentRow + rowOffset * k;
                int y = currentCol + colOffset * k;

                if (board.getPieces()[x][y].getColor() != color) {
                    System.out.println("True");
                    isWinner = false;
                    break;
                }
            }

            if (isWinner) {
                Player winner = color == Color.RED ? redPlayer : yellowPlayer;

                logger.info("Winner determined: " + winner.getFirstName() + " " + winner.getLastName());
                return winner;
            }

            currentRow += rowOffset;
            currentCol += colOffset;
        }

        return null;
    }
}