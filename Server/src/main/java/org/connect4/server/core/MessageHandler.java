package org.connect4.server.core;

import org.connect4.game.ai.enums.AIType;
import org.connect4.game.networking.Message;
import org.connect4.server.logging.ServerLogger;

import java.util.logging.Logger;

/**
 * A class that handles the client's messages.
 * @author Hassan
 */
public class MessageHandler {
    private static final Logger logger = ServerLogger.getLogger();

    private final ServerManager serverManager;
    private final ClientConnection clientConnection;

    /**
     * Constructs a message handler.
     * @param serverManager The server manager.
     * @param clientConnection The client connection.
     */
    public MessageHandler(ServerManager serverManager, ClientConnection clientConnection) {
        this.serverManager = serverManager;
        this.clientConnection = clientConnection;
    }

    /**
     * Handles the specified message.
     * @param message The message to be handled.
     */
    public void handleMessage(Message<?> message) {
        switch (message.getType()) {
            case MULTI_PLAYER_GAME -> {
                logger.info("Handles multi-player game request.");
                serverManager.handleMultiPlayerGameSession(clientConnection);
            }
            case SINGLE_PLAYER_GAME -> {
                logger.info("Handles single-player game request.");
                AIType aiType = (AIType) message.getPayload();
                serverManager.handleSinglePlayerGameSession(clientConnection, aiType);
            }
            case MOVE, TEXT -> {
                logger.info("Handles relay messages.");
                try {
                    clientConnection.getMessageQueue().put(message);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            case CLIENT_DISCONNECTED -> {
                logger.info("Handles client disconnection request.");
                serverManager.handleClientDisconnection(clientConnection);
            }
        }
    }
}
