package org.connect4.game.networking;

import java.io.Serializable;

/**
 * Enum representing the different types of messages exchanged in a Connect-4 Game.
 * @author Hassan
 */
public enum MessageType implements Serializable {
    START_GAME,
    COLOR,
    MOVE,
    TEXT,
    GAME_OVER,
    SERVER_STOPPED
}
