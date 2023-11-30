package org.connect4.game.core;

import org.connect4.game.exceptions.InvalidMoveException;
import org.connect4.game.enums.Color;
import org.connect4.game.enums.GameType;
import org.connect4.game.utils.WinnerChecker;

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

    private final Board board;
    private final Player redPlayer;
    private final Player yellowPlayer;
    private Player currentPlayer;
    private final GameType gameType;

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
    public Game(Board board, Player redPlayer, Player yellowPlayer, GameType gameType) {
        this.board = board;
        this.redPlayer = redPlayer;
        this.yellowPlayer = yellowPlayer;
        this.currentPlayer = redPlayer;
        this.gameType = gameType;
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
     * Gets the current player.
     * @return The current player.
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Gets the game type.
     * @return The game type.
     */
    public GameType getGameType() {
        return gameType;
    }

    /**
     * Makes a move for the current player on the specified column index and switches turns.
     * @param colIndex The index of the column to add the piece to.
     * @throws InvalidMoveException if the move is invalid.
     */
    public void performCurrentPlayerMove(int colIndex) throws InvalidMoveException {
        currentPlayer.makeMove(board, colIndex);
        switchTurn();
    }

    /**
     * Checks if the game is over.
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return board.isFull() || WinnerChecker.hasWinner(board);
    }

    /**
     * Gets the winner of the game if it has one.
     * @return The winner of the game or `null` if there is no winner yet.
     */
    public Player getWinner() {
        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLS; j++) {
                if (board.getPieces()[i][j] != null) {
                    Player rowWinner = determineWinner(i, 0, 0, 1);
                    Player colWinner = determineWinner(0, j, 1, 0);
                    Player leftDiagonalWinner = determineWinner(i, j, 1, 1);
                    Player rightDiagonalWinner = determineWinner(i, j, 1, -1);

                    if (rowWinner != null || colWinner != null || leftDiagonalWinner != null || rightDiagonalWinner != null) {
                        logger.info("Winner is determined at row: " + i + " and col: " + j);
                        return board.getPieces()[i][j].getColor() == Color.RED ? redPlayer : yellowPlayer;
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

        while (currentRow + WinnerChecker.CONSECUTIVE_PIECES_FOR_WIN * rowOffset >= 0
                && currentRow + WinnerChecker.CONSECUTIVE_PIECES_FOR_WIN * rowOffset < Board.ROWS
                && currentCol + WinnerChecker.CONSECUTIVE_PIECES_FOR_WIN * colOffset >= 0
                && currentCol + WinnerChecker.CONSECUTIVE_PIECES_FOR_WIN * colOffset < Board.COLS) {
            if (board.getPieces()[currentRow][currentCol] != null) {
                Color color = board.getPieces()[currentRow][currentCol].getColor();
                boolean isWinner = true;

                for (int k = 0; k < WinnerChecker.CONSECUTIVE_PIECES_FOR_WIN; k++) {
                    int x = currentRow + rowOffset * k;
                    int y = currentCol + colOffset * k;

                    if (board.getPieces()[x][y] == null || board.getPieces()[x][y].getColor() != color) {
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

            currentRow += rowOffset;
            currentCol += colOffset;
        }

        return null;
    }

    /**
     * Switches turns between players.
     */
    private void switchTurn() {
        currentPlayer = currentPlayer == redPlayer ? yellowPlayer : redPlayer;
        logger.info("Switched turns. Current player: " + currentPlayer.getFirstName() + " " + currentPlayer.getLastName());
    }
}