package org.connect4.client.core;

import org.connect4.client.logger.ClientLogger;
import org.connect4.game.networking.messaging.Message;
import org.connect4.game.networking.exceptions.ReceiveMessageFailureException;

/**
 * A class that listens to the messages sent by the server.
 * @author Hassan
 */
public class MessageListener implements Runnable {
    private static final ClientLogger LOGGER = ClientLogger.getLogger();

    private final ClientConnection clientConnection;
    private final MessageHandler messageHandler;

    /**
     * Constructs a new MessageListener.
     * @param clientConnection The client connection.
     * @param messageHandler The message handler.
     */
    public MessageListener(ClientConnection clientConnection, MessageHandler messageHandler) {
        this.clientConnection = clientConnection;
        this.messageHandler = messageHandler;
    }

    /**
     * Listens for incoming messages from the server.
     */
    @Override
    public void run() {
        try {
            while(clientConnection.isConnected()) {
                Message<?> message = clientConnection.receiveMessage();
                if (message != null) {
                    messageHandler.handleMessage(message);
                }
            }
        } catch (ReceiveMessageFailureException e) {
            LOGGER.severe("Failed to receive client message: " + e.getMessage());
        }
    }
}
