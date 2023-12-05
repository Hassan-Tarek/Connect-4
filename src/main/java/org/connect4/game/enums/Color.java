package org.connect4.game.enums;

/**
 * Enum representing the colors of game pieces.
 * @author Hassan
 */
public enum Color {
    RED,
    YELLOW;

    /**
     * Returns the opposite Color.
     * @return The opposite Color.
     */
    public Color opposite() {
        return this == RED ? YELLOW : RED;
    }
}
