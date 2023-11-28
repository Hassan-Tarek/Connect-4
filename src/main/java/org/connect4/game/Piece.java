package org.connect4.game;

import org.connect4.game.utils.Color;
import org.connect4.game.utils.Position;

/**
 * A class represents a board piece.
 * @author Hassan
 */
public class Piece {
    private final Position position;
    private final Color color;

    /**
     * Constructs a new piece with the specified position and color.
     * @param position The position of the piece on the game board.
     * @param color The color of the piece.
     */
    public Piece(Position position, Color color) {
        this.position = position;
        this.color = color;
    }

    /**
     * Gets the position of the piece on the game board.
     * @return The position of the piece.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Gets the color of the piece.
     * @return The color of the piece.
     */
    public Color getColor() {
        return color;
    }
}
