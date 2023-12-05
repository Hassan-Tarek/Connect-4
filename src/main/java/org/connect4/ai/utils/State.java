package org.connect4.ai.utils;

import org.connect4.game.core.Board;
import org.connect4.game.enums.Color;

/**
 * A class represents the state of the game.
 * @author hassan
 */
public class State implements Cloneable {
    private final Board board;
    private final Color playerColor;

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
     * Gets the color of the player making the move.
     * @return The player's color.
     */
    public Color getPlayerColor() {
        return playerColor;
    }

    /**
     * Creates a deep copy of the current game state.
     * @return A clone of the game state.
     */
    @Override
    public State clone() {
        Board clonedBoard = board.clone();
        return new State(clonedBoard, playerColor);
    }
}
