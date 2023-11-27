package org.connect4.game;

import java.io.IOException;
import java.util.logging.*;

public class Game {
    public static final Logger logger = Logger.getLogger(Game.class.getName());
    private static final int CONSECUTIVE_PIECES_FOR_WIN = 4;

    private final Board board;
    private final Player redPlayer;
    private final Player yellowPlayer;

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

    public Game(Board board, Player redPlayer, Player yellowPlayer) {
        this.board = board;
        this.redPlayer = redPlayer;
        this.yellowPlayer = yellowPlayer;
    }

    public Board getBoard() {
        return board;
    }

    public Player getRedPlayer() {
        return redPlayer;
    }

    public Player getYellowPlayer() {
        return yellowPlayer;
    }

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

    private boolean isRowWinner(int rowIndex) {
        return isLineWinner(rowIndex, Board.COLS - 1, true, false);
    }

    private boolean isColWinner(int colIndex) {
        return isLineWinner(Board.ROWS - 1, colIndex, false, false);
    }

    private boolean isDiagonalWinner(int rowIndex, int colIndex) {
        return isDiagonalWinnerLeftToRight(rowIndex, colIndex) ||
                isDiagonalWinnerRightToLeft(rowIndex, colIndex);
    }

    private boolean isDiagonalWinnerLeftToRight(int rowIndex, int colIndex) {
        return isLineWinner(rowIndex, colIndex, false, true);
    }

    private boolean isDiagonalWinnerRightToLeft(int rowIndex, int colIndex) {
        return isLineWinner(rowIndex, colIndex, false, false);
    }

    private boolean isLineWinner(int rowIndex, int colIndex, boolean isRow, boolean isLeftToRight) {
        final int OFFSET = isLeftToRight ? 1 : -1;

        Color lastColor = null;
        int count = 0;

        for (int i = rowIndex; i < Board.ROWS; i++) {
            for (int j = colIndex; j < Board.COLS; j += OFFSET) {
                int x = isRow ? i : j;
                int y = isRow ? j : i;

                if (board.getPieces()[x][y] != null) {
                    logger.log(Level.FINE, "Piece is not null.");

                    if (board.getPieces()[x][y].getColor() == lastColor) {
                        count++;

                        if (count >= CONSECUTIVE_PIECES_FOR_WIN) {
                            logger.info("Player has won the game.");
                            return true;
                        }
                    } else {
                        lastColor = board.getPieces()[x][y].getColor();
                        count = 1;
                    }
                } else {
                    lastColor = null;
                    count = 0;
                }
            }
        }

        return false;
    }

    public Player getWinner() {
        for (int i = 0; i < Board.ROWS; i++) {
            if (isRowWinner(i)) {
                logger.info("Row winner is determined at row: " + i);
                return determineWinner(i, 0, 1, 0);
            }
        }

        for (int i = 0; i < Board.COLS; i++) {
            if (isColWinner(i)) {
                logger.info("Column winner is determined at col: " + i);
                return determineWinner(0, i, 0, 1);
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

    private Player determineWinner(int rowIndex, int colIndex, int rowOffset, int colOffset) {
        for (int i = rowIndex; i + CONSECUTIVE_PIECES_FOR_WIN < Board.ROWS; i += rowOffset) {
            for (int j = colIndex; j + CONSECUTIVE_PIECES_FOR_WIN < Board.COLS; j += colOffset) {
                Color color = board.getPieces()[i][j].getColor();
                boolean isWinner = true;

                for (int k = 0; k < CONSECUTIVE_PIECES_FOR_WIN; k++) {
                    int x = i + rowOffset * k;
                    int y = j + colOffset * k;

                    if (board.getPieces()[x][y].getColor() != color) {
                        isWinner = false;
                        break;
                    }
                }

                if (isWinner) {
                    Player winner = color == Color.RED ? redPlayer : yellowPlayer;

                    logger.info("Winner determined: " + winner.getFirstName() + " " + winner.getLastName());
                    return winner;
                }
            }
        }
        
        return null;
    }
}
