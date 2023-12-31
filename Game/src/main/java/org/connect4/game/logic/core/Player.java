package org.connect4.game.logic.core;

import org.connect4.game.logic.exceptions.InvalidMoveException;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.enums.PlayerType;
import org.connect4.game.logging.GameLogger;

import java.util.logging.Logger;

/**
 * A class represents the game player.
 * @author Hassan
 */
public class Player {
    private static final Logger logger = GameLogger.getLogger();

    private final Color color;
    private final PlayerType playerType;
    private int score;

    /**
     * Constructs a new player with the specified parameters.
     * @param color The color of the players' pieces.
     * @param playerType The type of the player.
     */
    public Player(Color color, PlayerType playerType) {
        this.color = color;
        this.playerType = playerType;
        this.score = 0;
    }

    /**
     * Gets the players' pieces color.
     * @return The color of the player pieces.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the player's type.
     * @return The type of the player.
     */
    public PlayerType getPlayerType() {
        return playerType;
    }

    /**
     * Gets the current score of the player.
     * @return The current score of the player.
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the score of the player.
     * @param score The score of the player.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Resets the score of the player to zero.
     */
    public void resetScore() {
        logger.fine("Player's score has been reset to zero.");
        this.score = 0;
    }

    /**
     * Increases the player's score by one.
     */
    public void incrementScore() {
        logger.fine("Player's score has been increased.");
        this.score++;
    }

    /**
     * Makes a move on the specified column index on the specified board.
     * @param move The move which this player will make.
     * @throws InvalidMoveException if the move is invalid.
     */
    public void makeMove(Move move) throws InvalidMoveException {
        try {
            logger.fine("Player is making a move.");
            move.applyMove(this);
        } catch (InvalidMoveException ex) {
            logger.warning("Invalid move. " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Checks whether this player is the winner of the game or not.
     * @param game The game to determine if its winner is this player or not.
     * @return true if this player is the winner of the game, false otherwise.
     */
    public boolean isWin(Game game) {
        boolean isWin = game.getWinner() == this;

        if (isWin) {
            logger.info("This player won the game.");
        }

        return isWin;
    }
}
