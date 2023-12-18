package org.connect4.game.enums;

import java.io.Serializable;

/**
 * Enum representing the colors of game pieces.
 * @author Hassan
 */
public enum Color implements Serializable {
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
