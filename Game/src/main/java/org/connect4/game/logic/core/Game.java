package org.connect4.game.logic.core;

import org.connect4.game.logging.GameLogger;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.enums.GameType;
import org.connect4.game.logic.exceptions.InvalidMoveException;
import org.connect4.game.logic.utils.WinnerChecker;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * A class represents Connect-4 game.
 * @author Hassan
 */
public class Game {
    public static final Logger LOGGER = GameLogger.getLogger();

    private final Board board;
    private final Player redPlayer;
    private final Player yellowPlayer;
    private final GameType gameType;
    private final WinnerChecker winnerChecker;
    private Player currentPlayer;

    /**
     * Constructs a new game.
     * @param board The board of the game.
     * @param redPlayer The player with red pieces.
     * @param yellowPlayer The player with yellow pieces.
     * @param gameType The type of the game.
     */
    public Game(Board board, Player redPlayer, Player yellowPlayer, GameType gameType) {
        this.board = board;
        this.redPlayer = redPlayer;
        this.yellowPlayer = yellowPlayer;
        this.gameType = gameType;
        this.winnerChecker = new WinnerChecker(board);
        this.currentPlayer = redPlayer;
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
     * Gets the game type.
     * @return The game type.
     */
    public GameType getGameType() {
        return gameType;
    }

    /**
     * Gets the current player.
     * @return The current player.
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Makes a move for the current player on the specified column index and switches turns.
     * @param move The move will be made.
     */
    public void performCurrentPlayerMove(Move move) {
        try {
            if (move.isValid(board)) {
                LOGGER.info("Performing Current Player move.");
                board.addPiece(move.getColumn(), currentPlayer.getColor());
                switchTurn();
            }
        } catch (InvalidMoveException e) {
            LOGGER.severe("Invalid move: " + e.getMessage());
        }
    }

    /**
     * Checks if the game is over.
     * @return true if the game is over, false otherwise.
     */
    public boolean isOver() {
        return winnerChecker.hasWinner() || isDraw();
    }

    /**
     * Checks if the game is a draw.
     * @return true if the game is a draw, false otherwise.
     */
    public boolean isDraw() {
        return !winnerChecker.hasWinner() && board.isFull();
    }

    /**
     * Gets the winner of the game if it has one.
     * @return The winner of the game or `null` if there is no winner yet.
     */
    public Optional<Player> getWinner() {
        Color winnerColor = winnerChecker.determineWinner();
        return switch (winnerColor) {
            case RED -> Optional.of(redPlayer);
            case YELLOW -> Optional.of(yellowPlayer);
            case NONE -> Optional.empty();
        };
    }

    /**
     * Resets the game.
     */
    public void reset() {
        this.board.reset();
        this.currentPlayer = redPlayer;

        LOGGER.info("Game has been reset.");
    }

    /**
     * Switches turns between players.
     */
    private void switchTurn() {
        currentPlayer = currentPlayer == redPlayer ? yellowPlayer : redPlayer;
        LOGGER.info("Switched turns.");
        LOGGER.info("Current player: " + currentPlayer.getColor() + " Player.");
    }
}
