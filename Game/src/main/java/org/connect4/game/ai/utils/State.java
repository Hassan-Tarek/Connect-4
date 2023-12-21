package org.connect4.game.ai.utils;

import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.enums.Color;

/**
 * A class represents the state of the game.
 * @author hassan
 */
public class State implements Cloneable {
    private Board board;
    private Color playerColor;

    /**
     * Constructs a new game state with the specified board and player color.
     * @param board The current game board.
     * @param playerColor The color of the player making the move.
     */
    public State(Board board, Color playerColor) {
        this.board = board;
        this.playerColor = playerColor;
    }

    /**
     * Gets the current game board.
     * @return The game board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the current game board.
     * @param board The game board.
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Gets the color of the player making the move.
     * @return The player's color.
     */
    public Color getPlayerColor() {
        return playerColor;
    }

    /**
     * Sets the color of the player making the move.
     * @param playerColor The player's color.
     */
    public void setPlayerColor(Color playerColor) {
        this.playerColor = playerColor;
    }

    /**
     * Creates a deep copy of the current game state.
     * @return A clone of the game state.
     */
    @Override
    public State clone() {
        try {
            State clonedState = (State) super.clone();
            this.setBoard(clonedState.board.clone());
            return clonedState;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
