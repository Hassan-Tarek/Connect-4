package org.connect4.game.networking.messaging;

import java.io.Serializable;

/**
 * Enum representing the different types of server messages exchanged in a Connect-4 Game.
 * @author Hassan
 */
public enum ServerMessageType implements MessageType, Serializable {
    GAME_STARTED,
    COLOR,
    MOVE,
    TEXT,
    PLAYER_TURN,
    GAME_OVER,
    REMATCH_REQUEST,
    GAME_SESSION_ENDED,
    DISCONNECT_COMPLETED,
    SERVER_STOPPED;

    @Override
    public String getName() {
        return this.name();
    }
}
