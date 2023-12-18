package org.connect4.networking.shared;

public class Message<T> {
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
