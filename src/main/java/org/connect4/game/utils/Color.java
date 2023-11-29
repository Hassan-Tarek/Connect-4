package org.connect4.game.utils;

/**
 * Enum representing the colors of game pieces.
 * @author Hassan
 */
public enum Color {
    RED,
    YELLOW;

    public Color opposite() {
        return this == RED ? YELLOW : RED;
    }
}
