package org.connect4.game.networking;

import java.io.Serializable;

/**
 * Enum representing the different types of messages exchanged in a Connect-4 Game.
 * @author Hassan
 */
public enum MessageType implements Serializable {
    START_GAME,
    MULTI_PLAYER_GAME,
    SINGLE_PLAYER_GAME,
    PLAY_AGAIN,
    COLOR,
    MOVE,
    TEXT,
    PLAYER_TURN,
    GAME_OVER,
    GAME_STOPPED,
    SERVER_STOPPED,
    CLIENT_DISCONNECTED
}
