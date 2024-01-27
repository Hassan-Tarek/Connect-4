package org.connect4.client.core;

import org.connect4.game.networking.messaging.Message;
import org.connect4.game.networking.messaging.ServerMessageType;

/**
 * A class that handles the messages received from server.
 * @author Hassan
 */
public class MessageHandler {
    private static final ClientLogger LOGGER = ClientLogger.getLogger();

    private final ClientConnection clientConnection;
    private final MessageSender messageSender;

    /**
     * Constructs a new MessageHandler with the specified client connection, message sender.
     * @param clientConnection The client connection.
     * @param messageSender The message sender.
     */
    public MessageHandler(ClientConnection clientConnection, MessageSender messageSender) {
        this.clientConnection = clientConnection;
        this.messageSender = messageSender;
    }

    /**
     * Handles an incoming message from the server.
     * @param message The message received from the server.
     * @param <T> The type of the message payload.
     */
    public <T> void handleMessage(Message<T> message) {
        ServerMessageType serverMessageType = message.getType();
        System.out.println("Handling messages.");
    }
}
