package org.connect4.game.networking;

import java.io.Serializable;

public enum MessageType implements Serializable {
    START_GAME,
    COLOR,
    MOVE,
    TEXT,
    GAME_OVER,
    SERVER_STOPPED
}
