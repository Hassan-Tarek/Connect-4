package org.connect4.server.core.network;

import org.connect4.game.ai.enums.AIType;
import org.connect4.game.logic.core.Move;
import org.connect4.game.networking.messaging.ClientMessageType;
import org.connect4.game.networking.messaging.Message;
import org.connect4.server.core.ClientManager;
import org.connect4.server.core.GameSessionManager;
import org.connect4.server.core.ServerManager;
import org.connect4.server.core.session.GameSession;
import org.connect4.server.core.session.GameSessionType;
import org.connect4.server.core.session.MultiPlayerGameSession;
import org.connect4.server.core.session.SinglePlayerGameSession;
import org.connect4.server.logging.ServerLogger;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * A class that handles the client's messages.
 * @author Hassan
 */
public class ClientMessageHandler {
    private static final Logger LOGGER = ServerLogger.getLogger();

    private final ClientConnection clientConnection;
    private final ClientManager clientManager;
    private final GameSessionManager gameSessionManager;
    private final MessageDispatcher messageDispatcher;
    private final ExecutorService executorService;

    /**
     * Constructs a message handler.
     * @param clientConnection The client connection.
     * @param serverManager The server manager.
     */
    public ClientMessageHandler(ClientConnection clientConnection, ServerManager serverManager) {
        this.clientConnection = clientConnection;
        this.clientManager = serverManager.getClientManager();
        this.gameSessionManager = serverManager.getGameSessionManager();
        this.messageDispatcher = new MessageDispatcher();
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
                case REMATCH_REQUEST -> handleRematchRequest((Message<Void>) message);
                case MOVE -> handleMoveMessage((Message<Move>) message);
                case TEXT -> handleTextMessage((Message<String>) message);
                case LEAVE_GAME_SESSION_REQUEST -> handleLeaveGameSessionRequest((Message<Void>) message);
                case DISCONNECT_REQUEST -> handleClientDisconnectRequest((Message<Void>) message);
            }
        });
    }

    /**
     * Handles the multi-player game request.
     * @param multiPlayerGameRequestMessage The multi-player game request message.
     */
    private void handleMultiPlayerGameRequest(Message<Void> multiPlayerGameRequestMessage) {
        LOGGER.info("Handles: " + multiPlayerGameRequestMessage.getType().getName());

        Optional<ClientConnection> matchedClientConnection = clientManager.findMatchedClient(clientConnection);
        if (matchedClientConnection.isPresent()) {
            LOGGER.info("A match has been found!");

            GameSession gameSession = new MultiPlayerGameSession(clientConnection, matchedClientConnection.get(), messageDispatcher);
            gameSessionManager.startGameSession(gameSession);
        } else {
            LOGGER.info("No match has been found!");

            clientManager.addClientToWaitingList(clientConnection);
            messageDispatcher.sendWaitingForOpponent(clientConnection);
        }
    }

    /**
     * Handles the single-player game request.
     * @param singlePlayerGameRequestMessage The single-player game request message.
     */
    private void handleSinglePlayerGameRequest(Message<AIType> singlePlayerGameRequestMessage) {
        LOGGER.info("Handles: " + singlePlayerGameRequestMessage.getType().getName());

        AIType aiType = singlePlayerGameRequestMessage.getPayload();
        GameSession gameSession = new SinglePlayerGameSession(clientConnection, aiType, messageDispatcher);

        gameSessionManager.startGameSession(gameSession);
    }

    /**
     * Handles the rematch response.
     * @param rematchRequestMessage The rematch request message.
     */
    private void handleRematchRequest(Message<Void> rematchRequestMessage) {
        LOGGER.info("Handles: " + rematchRequestMessage.getType().getName());

        LOGGER.info("Received rematch response from client: " + clientConnection);
        GameSession gameSession = gameSessionManager.getGameSessionForClient(clientConnection);
        gameSession.getCountDownLatch().countDown();
        try {
            if (gameSession.getCountDownLatch().await(30, TimeUnit.SECONDS)) {
                gameSessionManager.restartGameSession(gameSession);
            } else {
                LOGGER.info("Timeout occurred: One or both players did not request a rematch.");

                messageDispatcher.broadcastGameSessionEnded(gameSession.getClients());
                gameSessionManager.removeGameSession(gameSession);
            }
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupted while waiting for players to respond: " + e.getMessage());
        }
    }

    /**
     * Handles the move message.
     * @param moveMessage The move message.
     */
    private void handleMoveMessage(Message<Move> moveMessage) {
        try {
            LOGGER.info("Handles: " + moveMessage.getType().getName());
            clientConnection.getMoveMessageQueue().put(moveMessage);
        } catch (InterruptedException e) {
            LOGGER.severe("Failed to enqueue move message: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Handles the text message.
     * @param textMessage The text message.
     */
    private void handleTextMessage(Message<String> textMessage) {
        try {
            LOGGER.info("Handles: " + textMessage.getType().getName());
            clientConnection.getTextMessageQueue().put(textMessage);
        } catch (InterruptedException e) {
            LOGGER.severe("Failed to enqueue text message: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Handles the leave game session request.
     * @param leaveGameSessionRequestMessage The leave game session request message.
     */
    private void handleLeaveGameSessionRequest(Message<Void> leaveGameSessionRequestMessage) {
        LOGGER.info("Handles: " + leaveGameSessionRequestMessage.getType().getName());

        GameSession gameSession = gameSessionManager.getGameSessionForClient(clientConnection);
        if (gameSession != null) {
            gameSession.shutdown();
            gameSessionManager.removeGameSession(gameSession);
            clientManager.addClientsToWaitingList(gameSession.getClients());
        }
    }

    /**
     * Handles the client disconnect request.
     * @param disconnectRequestMessage The client disconnect request message.
     */
    private void handleClientDisconnectRequest(Message<Void> disconnectRequestMessage) {
        LOGGER.info("Handles: " + disconnectRequestMessage.getType().getName());

        GameSession gameSession = gameSessionManager.getGameSessionForClient(clientConnection);
        if (gameSession != null) {
            gameSession.shutdown();
            gameSessionManager.removeGameSession(gameSession);
            clientManager.removeConnectedClient(clientConnection);

            if (gameSession.getType() == GameSessionType.MULTI_PLAYER_GAME_SESSION) {
                ClientConnection opponentPlayerConnection = ((MultiPlayerGameSession) gameSession).getOpponentPlayerConnection(clientConnection);
                clientManager.addClientToWaitingList(opponentPlayerConnection);
                messageDispatcher.sendOpponentDisconnected(opponentPlayerConnection);
            }
        } else {
            clientManager.removeConnectedClient(clientConnection);
            clientManager.removeClientFromWaitingList(clientConnection);
        }

        messageDispatcher.sendDisconnectCompleted(clientConnection);
        clientConnection.disconnect();
        LOGGER.info("Client with address: %s has been disconnected.".formatted(clientConnection.getClientSocket().getRemoteSocketAddress()));
    }

    /**
     * Shutdown this message handler.
     */
    public void shutdown() {
        try {
            if (!executorService.isShutdown()) {
                executorService.shutdown();

                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            }

            LOGGER.info("Message handler shut down successfully.");
        } catch (InterruptedException e) {
            LOGGER.severe("Failed to shutdown the message handler: " + e.getMessage());
        }
    }
}
