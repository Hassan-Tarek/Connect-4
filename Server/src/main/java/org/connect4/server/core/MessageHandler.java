package org.connect4.server.core;

import org.connect4.game.ai.enums.AIType;
import org.connect4.game.logic.core.Move;
import org.connect4.game.networking.Message;
import org.connect4.server.logging.ServerLogger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * A class that handles the client's messages.
 * @author Hassan
 */
public class MessageHandler {
    private static final Logger logger = ServerLogger.getLogger();

    private final ServerManager serverManager;
    private final ClientConnection clientConnection;
    private final ExecutorService executorService;

    /**
     * Constructs a message handler.
     * @param serverManager The server manager.
     * @param clientConnection The client connection.
     */
    public MessageHandler(ServerManager serverManager, ClientConnection clientConnection) {
        this.serverManager = serverManager;
        this.clientConnection = clientConnection;
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * Handles the specified message.
     * @param message The message to be handled.
     */
    @SuppressWarnings("unchecked")
    public void handleMessage(Message<?> message) {
        executorService.submit(() -> {
            switch (message.getType()) {
                case MULTI_PLAYER_GAME -> handleMultiPlayerGameRequest();
                case SINGLE_PLAYER_GAME -> handleSinglePlayerGameRequest((Message<AIType>) message);
                case PLAY_AGAIN -> handlePlayAgainRequest();
                case MOVE -> handleMoveMessage((Message<Move>) message);
                case TEXT -> handleTextMessage((Message<String>) message);
                case CLIENT_DISCONNECTED -> handleClientDisconnectionRequest();
            }
        });
    }

    /**
     * Handles the multi-player game request.
     */
    private void handleMultiPlayerGameRequest() {
        logger.info("Handles multi-player game request.");
        serverManager.handleMultiPlayerGameSession(clientConnection);
    }

    /**
     * Handles the single-player game request.
     * @param message the message sent to the server.
     */
    private void handleSinglePlayerGameRequest(Message<AIType> message) {
        logger.info("Handles single-player game request.");
        AIType aiType = message.getPayload();
        serverManager.handleSinglePlayerGameSession(clientConnection, aiType);
    }

    /**
     * Handles the play-again request.
     */
    private void handlePlayAgainRequest() {
        logger.info("Handles play again request.");
        serverManager.handlePlayAgainRequest(clientConnection);
    }

    /**
     * Handles the move message.
     * @param moveMessage The move message to handle.
     */
    private void handleMoveMessage(Message<Move> moveMessage) {
        try {
            logger.info("Handles move messages.");
            clientConnection.getMoveMessageQueue().put(moveMessage);
        } catch (InterruptedException e) {
            logger.severe("Failed to enqueue move message: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Handles the text message.
     * @param textMessage The text message to handle.
     */
    private void handleTextMessage(Message<String> textMessage) {
        try {
            logger.info("Handles text messages.");
            clientConnection.getTextMessageQueue().put(textMessage);
        } catch (InterruptedException e) {
            logger.severe("Failed to enqueue move message: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Handles the client disconnection request.
     */
    private void handleClientDisconnectionRequest() {
        logger.info("Handles client disconnection request.");
        serverManager.handleClientDisconnection(clientConnection);
    }
}
