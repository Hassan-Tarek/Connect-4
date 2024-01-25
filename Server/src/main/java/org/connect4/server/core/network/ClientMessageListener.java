package org.connect4.server.core.network;

import org.connect4.game.networking.exceptions.ReceiveMessageFailureException;
import org.connect4.game.networking.messaging.Message;
import org.connect4.server.core.ServerManager;
import org.connect4.server.logging.ServerLogger;

import java.util.logging.Logger;

/**
 * A class that listen to messages sent by the client.
 * @author Hassan
 */
public class ClientMessageListener implements Runnable {
    private static final Logger LOGGER = ServerLogger.getLogger();

    private final ClientConnection clientConnection;
    private final ClientMessageHandler clientMessageHandler;

    /**
     * Constructs the MessageListener for the specified client connection.
     * @param clientConnection The client connection.
     * @param clientMessageHandler The client message handler.
     */
    public ClientMessageListener(ClientConnection clientConnection, ClientMessageHandler clientMessageHandler) {
        this.clientConnection = clientConnection;
        this.clientMessageHandler = clientMessageHandler;
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
                    clientMessageHandler.handleMessage(message);
                }
            }
        } catch (ReceiveMessageFailureException e) {
            LOGGER.severe("Failed to receive client message: " + e.getMessage());
        }
    }
}
