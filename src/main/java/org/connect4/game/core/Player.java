package org.connect4.game.core;

import org.connect4.game.exceptions.InvalidMoveException;
import org.connect4.game.enums.Color;
import org.connect4.game.enums.PlayerType;

import java.util.logging.Logger;

/**
 * A class represents the game player.
 * @author Hassan
 */
public class Player {
    private static final Logger logger = Game.logger;

    private String firstName;
    private String lastName;
    private final PlayerType playerType;
    private final Color color;
    private int score;

    /**
     * Constructs a new player with the specified parameters.
     * @param firstName The first name of the player.
     * @param lastName The last name of the player.
     * @param color The color of the players' pieces.
     * @param playerType The type of the player.
     */
    public Player(String firstName, String lastName, Color color, PlayerType playerType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.playerType = playerType;
        this.color = color;
        this.score = 0;
    }

    /**
     * Gets the first name of the player.
     * @return The first name of the player.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the player.
     * @param firstName The first name of the player.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the player.
     * @return The last name of the player.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the player.
     * @param lastName The last name of the player.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the player's type.
     * @return The type of the player.
     */
    public PlayerType getPlayerType() {
        return playerType;
    }

    /**
     * Gets the players' pieces color.
     * @return The color of the player pieces.
     */
    public Color getColor() {
        return color;
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
        logger.info("Player's score has been reset to zero.");
        this.score = 0;
    }

    /**
     * Increases the player's score by one.
     */
    public void incrementScore() {
        logger.info("Player's score has been increased.");
        this.score++;
    }

    /**
     * Makes a move on the specified column index on the specified board.
     * @param board The board of the game.
     * @param colIndex The index of the column to add piece on.
     * @throws InvalidMoveException if the move is invalid.
     */
    public void makeMove(Board board, int colIndex) throws InvalidMoveException {
        try {
            logger.info("Player is making a move.");
            board.addPiece(colIndex, color);
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
