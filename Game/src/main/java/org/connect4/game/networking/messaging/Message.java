package org.connect4.game.networking.messaging;

import java.io.Serial;
import java.io.Serializable;

/**
 * A class represents a message for client/server communication in a Connect-4 Game.
 * @param <T> The type of the message.
 * @author Hassan
 */
public class Message<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final MessageType type;
    private final T payload;

    /**
     * Constructs a new Message with the specified type and payload.
     * @param type The type of the message.
     * @param payload The payload of the message.
     */
    public Message(MessageType type, T payload) {
        this.type = type;
        this.payload = payload;
    }

    /**
     * Gets the type of the message.
     * @return The message type.
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Gets the payload of the message.
     * @return The message payload.
     */
    public T getPayload() {
        return payload;
    }
}
