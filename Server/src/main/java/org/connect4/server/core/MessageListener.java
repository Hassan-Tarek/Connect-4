package org.connect4.server.core;

import org.connect4.game.networking.Message;
import org.connect4.game.networking.exceptions.ReceiveMessageFailureException;
import org.connect4.server.logging.ServerLogger;

import java.util.logging.Logger;

/**
 * A class that listen to messages sent by the client.
 * @author Hassan
 */
public class MessageListener implements Runnable {
    private static final Logger logger = ServerLogger.getLogger();

    private final ClientConnection clientConnection;
    private final MessageHandler messageHandler;

    /**
     * Constructs the MessageListener for the specified client connection.
     * @param clientConnection The client connection.
     * @param serverManager The server manager.
     */
    public MessageListener(ClientConnection clientConnection, ServerManager serverManager) {
        this.clientConnection = clientConnection;
        this.messageHandler = new MessageHandler(serverManager, clientConnection);
    }

    /**
     * Listens for incoming messages from the client connection.
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
            logger.severe("Failed to receive client message: " + e.getMessage());
        }
    }
}
