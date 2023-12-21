package org.connect4.game.logic.core;

import org.connect4.game.logic.exceptions.InvalidMoveException;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.enums.GameType;
import org.connect4.game.logic.utils.WinnerChecker;
import org.connect4.game.logging.GameLogger;

import java.util.logging.Logger;

/**
 * A class represents Connect-4 game.
 * @author Hassan
 */
public class Game {
    public static final Logger logger = GameLogger.getLogger();

    private final Board board;
    private final Player redPlayer;
    private final Player yellowPlayer;
    private Player currentPlayer;
    private final GameType gameType;

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
     * @param move The move will be made.
     * @throws InvalidMoveException if the move is invalid.
     */
    public void performCurrentPlayerMove(Move move) throws InvalidMoveException {
        currentPlayer.makeMove(move);
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
                if (board.getPieceAt(i, j) != null) {
                    Player rowWinner = determineWinner(i, 0, 0, 1);
                    Player colWinner = determineWinner(0, j, 1, 0);
                    Player leftDiagonalWinner = determineWinner(i, j, 1, 1);
                    Player rightDiagonalWinner = determineWinner(i, j, 1, -1);

                    if (rowWinner != null || colWinner != null || leftDiagonalWinner != null || rightDiagonalWinner != null) {
                        logger.info("Winner is determined at row: " + i + " and col: " + j);
                        return board.getPieceAt(i, j).getColor() == Color.RED ? redPlayer : yellowPlayer;
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
            if (board.getPieceAt(currentRow, currentCol) != null) {
                Color color = board.getPieceAt(currentRow, currentCol).getColor();
                boolean isWinner = true;

                for (int k = 0; k < WinnerChecker.CONSECUTIVE_PIECES_FOR_WIN; k++) {
                    int x = currentRow + rowOffset * k;
                    int y = currentCol + colOffset * k;

                    if (board.getPieceAt(x, y) == null || board.getPieceAt(x, y).getColor() != color) {
                        isWinner = false;
                        break;
                    }
                }

                if (isWinner) {
                    Player winner = color == Color.RED ? redPlayer : yellowPlayer;

                    logger.info("Winner determined: " + winner.getUsername());
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
        logger.fine("Switched turns. Current player: " + currentPlayer.getUsername());
    }
}
