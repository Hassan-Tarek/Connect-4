package org.connect4.game.networking;

import java.io.Serial;
import java.io.Serializable;

public class Message<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final MessageType type;
    private final T payload;

    public Message(MessageType type, T payload) {
        this.type = type;
        this.payload = payload;
    }

    public MessageType getType() {
        return type;
    }

    public T getPayload() {
        return payload;
    }
}
