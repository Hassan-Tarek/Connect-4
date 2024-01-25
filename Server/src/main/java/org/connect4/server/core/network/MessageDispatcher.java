package org.connect4.server.core.network;

import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.networking.exceptions.SendMessageFailureException;
import org.connect4.game.networking.messaging.Message;
import org.connect4.game.networking.messaging.ServerMessageType;
import org.connect4.server.logging.ServerLogger;

import java.util.List;

/**
 * A class responsible for dispatching messages to clients.
 * @author Hassan
 */
public class MessageDispatcher {
    private static final ServerLogger LOGGER = ServerLogger.getLogger();

    /**
     * Sends a message to the specified client connection.
     * @param clientConnection The client connection to send message to.
     * @param messageType The type of the message.
     * @param payload The payload of the message.
     * @param <T> The type of the payload.
     */
    private <T> void sendMessage(ClientConnection clientConnection, ServerMessageType messageType, T payload) {
        Message<T> message = new Message<>(messageType, payload);
        try {
            clientConnection.sendMessage(message);
        } catch (SendMessageFailureException e) {
            LOGGER.severe("Failed to send [%s] to client [%s]: %s"
                    .formatted(messageType.getName(), clientConnection, e.getMessage()));
        }
    }

    /**
     * Sends waiting for opponent message to the specified client connection.
     * @param clientConnection The client connection.
     */
    public void sendWaitingForOpponent(ClientConnection clientConnection) {
        sendMessage(clientConnection, ServerMessageType.WAITING_FOR_OPPONENT, null);
    }

    /**
     * Sends the specified assigned color to the specified client connection.
     * @param clientConnection The client connection.
     * @param color The assigned color.
     */
    public void sendAssignedColor(ClientConnection clientConnection, Color color) {
        sendMessage(clientConnection, ServerMessageType.COLOR, color);
    }

    /**
     * Sends chat text to the specified client connection.
     * @param clientConnection The client connection.
     * @param chatText The chat text.
     */
    public void sendPlayerChat(ClientConnection clientConnection, String chatText) {
        sendMessage(clientConnection, ServerMessageType.TEXT, chatText);
    }

    /**
     * Sends disconnect completed message to the specified client connection.
     * @param clientConnection The client connection.
     */
    public void sendDisconnectCompleted(ClientConnection clientConnection) {
        sendMessage(clientConnection, ServerMessageType.DISCONNECT_COMPLETED, null);
    }

    /**
     * Sends opponent disconnected message to the specified client connection.
     * @param clientConnection The client connection.
     */
    public void sendOpponentDisconnected(ClientConnection clientConnection) {
        sendMessage(clientConnection, ServerMessageType.OPPONENT_DISCONNECTED, null);
    }

    /**
     * Broadcasts a message to a list of client connections.
     * @param clientConnections The client connections to broadcast the message to.
     * @param messageType The type of the message.
     * @param payload The payload of the message.
     * @param <T> The type of the payload.
     */
    private <T> void broadcastMessage(List<ClientConnection> clientConnections, ServerMessageType messageType, T payload) {
        for (ClientConnection clientConnection : clientConnections) {
            sendMessage(clientConnection, messageType, payload);
        }
    }

    /**
     * Broadcast game started message to the specified list of client connections.
     * @param clientConnections The client connections.
     */
    public void broadcastGameStarted(List<ClientConnection> clientConnections) {
        broadcastMessage(clientConnections, ServerMessageType.GAME_STARTED, null);
    }

    /**
     * Broadcast the specified player scores to the specified list of client connections.
     * @param clientConnections The client connections.
     * @param redPlayerScore The red player score.
     * @param yellowPlayerScore The yellow player score.
     */
    public void broadcastPlayerScores(List<ClientConnection> clientConnections, int redPlayerScore, int yellowPlayerScore) {
        int[] scores = new int[] {redPlayerScore, yellowPlayerScore};
        broadcastMessage(clientConnections, ServerMessageType.PLAYER_SCORES, scores);
    }

    /**
     * Broadcast the specified player move to the specified list of client connections.
     * @param clientConnections The client connections.
     * @param move The player move.
     */
    public void broadcastPlayerMove(List<ClientConnection> clientConnections, Move move) {
        broadcastMessage(clientConnections, ServerMessageType.MOVE, move);
    }

    /**
     * Broadcast player turn to the specified list of client connections.
     * @param clientConnections The client connections.
     * @param currentPlayerColor The current player color.
     */
    public void broadcastPlayerTurn(List<ClientConnection> clientConnections, Color currentPlayerColor) {
        broadcastMessage(clientConnections, ServerMessageType.PLAYER_TURN, currentPlayerColor);
    }

    /**
     * Broadcast game over message to the specified list of client connections.
     * @param clientConnections The client connections.
     * @param winnerColor The color of the winner.
     */
    public void broadcastGameOver(List<ClientConnection> clientConnections, Color winnerColor) {
        broadcastMessage(clientConnections, ServerMessageType.GAME_OVER, winnerColor);
    }

    /**
     * Broadcast game session ended to the specified list of client connections.
     * @param clientConnections The client connections.
     */
    public void broadcastGameSessionEnded(List<ClientConnection> clientConnections) {
        broadcastMessage(clientConnections, ServerMessageType.GAME_SESSION_ENDED, null);
    }

    /**
     * Broadcast server stopped message to the specified list of client connections.
     * @param clientConnections The client connections.
     */
    public void broadcastServerStopped(List<ClientConnection> clientConnections) {
        broadcastMessage(clientConnections, ServerMessageType.SERVER_STOPPED, null);
    }
}
