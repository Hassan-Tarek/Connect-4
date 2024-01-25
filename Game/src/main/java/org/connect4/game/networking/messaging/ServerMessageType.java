package org.connect4.game.networking.messaging;

import java.io.Serializable;

/**
 * Enum representing the different types of server messages exchanged in a Connect-4 Game.
 * @author Hassan
 */
public enum ServerMessageType implements MessageType, Serializable {
    WAITING_FOR_OPPONENT,
    GAME_STARTED,
    COLOR,
    PLAYER_SCORES,
    MOVE,
    TEXT,
    PLAYER_TURN,
    GAME_OVER,
    GAME_SESSION_ENDED,
    DISCONNECT_COMPLETED,
    OPPONENT_DISCONNECTED,
    SERVER_STOPPED;

    /**
     * Gets the name of the enum constant.
     * @return The name of the enum constant.
     */
    @Override
    public String getName() {
        return this.name();
    }
}
