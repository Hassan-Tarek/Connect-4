package org.connect4.game.core;

import org.connect4.game.enums.Color;

import java.io.Serial;
import java.io.Serializable;

/**
 * A class represents a board piece.
 * @author Hassan
 */
public class Piece implements Cloneable, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

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

    /**
     * Creates and returns a copy of this Piece object.
     * @return A new Piece object that is a copy of this instance.
     */
    @Override
    public Piece clone() {
        Position clonedPosition = position.clone();
        return new Piece(clonedPosition, color);
    }
}
