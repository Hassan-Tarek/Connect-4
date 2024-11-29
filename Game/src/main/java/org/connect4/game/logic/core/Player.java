package org.connect4.game.logic.core;

import org.connect4.game.logging.GameLogger;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.enums.PlayerType;

import java.util.logging.Logger;

/**
 * A class represents the game player.
 * @author Hassan
 */
public class Player {
    private static final Logger LOGGER = GameLogger.getLogger();

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
        LOGGER.fine("Player's score has been reset to zero.");
        this.score = 0;
    }

    /**
     * Increases the player's score by one.
     */
    public void incrementScore() {
        LOGGER.fine("Player's score has been increased.");
        this.score++;
    }

    /**
     * Checks whether this player is the winner of the game or not.
     * @param game The game to determine if its winner is this player or not.
     * @return true if this player is the winner of the game, false otherwise.
     */
    public boolean isWinner(Game game) {
        boolean isWin = game.getWinner().map(winner -> winner == this).orElse(false);

        if (isWin) {
            LOGGER.info("This player won the game.");
        }

        return isWin;
    }
}
