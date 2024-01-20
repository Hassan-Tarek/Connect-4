package org.connect4.game.networking.messaging;

import java.io.Serializable;

/**
 * Enum representing the different types of client messages exchanged in a Connect-4 Game.
 * @author Hassan
 */
public enum ClientMessageType implements MessageType, Serializable {
    MULTI_PLAYER_GAME_REQUEST,
    SINGLE_PLAYER_GAME_REQUEST,
    REMATCH_RESPONSE,
    MOVE,
    TEXT,
    LEAVE_GAME_SESSION_REQUEST,
    DISCONNECT_REQUEST;

    /**
     * Gets the name of the enum constant.
     * @return The name of the enum constant.
     */
    public String getName() {
        return this.name();
    }
}
