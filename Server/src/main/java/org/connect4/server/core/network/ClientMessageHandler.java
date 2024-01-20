package org.connect4.server.core.network;

import org.connect4.game.ai.enums.AIType;
import org.connect4.game.logic.core.Move;
import org.connect4.game.networking.messaging.ClientMessageType;
import org.connect4.game.networking.messaging.Message;
import org.connect4.server.core.ServerManager;
import org.connect4.server.logging.ServerLogger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * A class that handles the client's messages.
 * @author Hassan
 */
public class ClientMessageHandler {
    private static final Logger logger = ServerLogger.getLogger();

    private final ServerManager serverManager;
    private final ClientConnection clientConnection;
    private final ExecutorService executorService;

    /**
     * Constructs a message handler.
     * @param serverManager The server manager.
     * @param clientConnection The client connection.
     */
    public ClientMessageHandler(ServerManager serverManager, ClientConnection clientConnection) {
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
            ClientMessageType clientMessageType = (ClientMessageType) message.getType();
            switch (clientMessageType) {
                case MULTI_PLAYER_GAME_REQUEST -> handleMultiPlayerGameRequest((Message<Void>) message);
                case SINGLE_PLAYER_GAME_REQUEST -> handleSinglePlayerGameRequest((Message<AIType>) message);
                case REMATCH_RESPONSE -> handleRematchResponse((Message<Void>) message);
                case MOVE -> handleMoveMessage((Message<Move>) message);
                case TEXT -> handleTextMessage((Message<String>) message);
                case LEAVE_GAME_SESSION_REQUEST -> handleLeaveGameSessionRequest((Message<Void>) message);
                case DISCONNECT_REQUEST -> handleClientDisconnectionRequest((Message<Void>) message);
            }
        });
    }

    /**
     * Handles the multi-player game request.
     * @param multiPlayerGameRequestMessage The multi-player game request message.
     */
    private void handleMultiPlayerGameRequest(Message<Void> multiPlayerGameRequestMessage) {
        logger.info("Handles: " + multiPlayerGameRequestMessage.getType().getName());
        serverManager.handleMultiPlayerGameSession(clientConnection);
    }

    /**
     * Handles the single-player game request.
     * @param singlePlayerGameRequestMessage The single-player game request message.
     */
    private void handleSinglePlayerGameRequest(Message<AIType> singlePlayerGameRequestMessage) {
        logger.info("Handles: " + singlePlayerGameRequestMessage.getType().getName());
        AIType aiType = singlePlayerGameRequestMessage.getPayload();
        serverManager.handleSinglePlayerGameSession(clientConnection, aiType);
    }

    /**
     * Handles the rematch response.
     * @param rematchResponseMessage The rematch response message.
     */
    private void handleRematchResponse(Message<Void> rematchResponseMessage) {
        logger.info("Handles: " + rematchResponseMessage.getType().getName());
        serverManager.handleRematchResponse(clientConnection);
    }

    /**
     * Handles the move message.
     * @param moveMessage The move message.
     */
    private void handleMoveMessage(Message<Move> moveMessage) {
        try {
            logger.info("Handles: " + moveMessage.getType().getName());
            clientConnection.getMoveMessageQueue().put(moveMessage);
        } catch (InterruptedException e) {
            logger.severe("Failed to enqueue move message: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Handles the text message.
     * @param textMessage The text message.
     */
    private void handleTextMessage(Message<String> textMessage) {
        try {
            logger.info("Handles: " + textMessage.getType().getName());
            clientConnection.getTextMessageQueue().put(textMessage);
        } catch (InterruptedException e) {
            logger.severe("Failed to enqueue text message: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Handles the leave game session request.
     * @param leaveGameSessionRequestMessage The leave game session request message.
     */
    private void handleLeaveGameSessionRequest(Message<Void> leaveGameSessionRequestMessage) {
        logger.info("Handles: " + leaveGameSessionRequestMessage.getType().getName());
        serverManager.handleLeaveGameSessionRequest(clientConnection);
    }

    /**
     * Handles the client disconnection request.
     * @param disconnectRequestMessage The client disconnect request message.
     */
    private void handleClientDisconnectionRequest(Message<Void> disconnectRequestMessage) {
        logger.info("Handles: " + disconnectRequestMessage.getType().getName());
        serverManager.handleClientDisconnectRequest(clientConnection);
    }

    /**
     * Shutdown this message handler.
     */
    public void shutdown() {
        try {
            if (!executorService.isShutdown()) {
                executorService.shutdown();

                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            }
        } catch (InterruptedException e) {
            logger.severe("Failed to shutdown the message handler: " + e.getMessage());
        }
    }
}
