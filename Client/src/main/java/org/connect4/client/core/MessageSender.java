package org.connect4.client.core;

import org.connect4.client.logger.ClientLogger;
import org.connect4.game.ai.enums.AIType;
import org.connect4.game.logic.core.Move;
import org.connect4.game.networking.exceptions.SendMessageFailureException;
import org.connect4.game.networking.messaging.ClientMessageType;
import org.connect4.game.networking.messaging.Message;

/**
 * A class that sends messages to the server.
 * @author Hassan
 */
public class MessageSender {
    private static final ClientLogger LOGGER = ClientLogger.getLogger();

    private final ClientConnection clientConnection;

    /**
     * Constructs a new ClientMessageSender with the specified client connection.
     * @param clientConnection The client connection.
     */
    public MessageSender(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    /**
     * Sends a message to the server.
     * @param clientMessageType The type of the message.
     * @param payload The payload of the message.
     * @param <T> The type of the payload.
     */
    private <T> void sendMessage(ClientMessageType clientMessageType, T payload) {
        try {
            Message<?> message = new Message<>(clientMessageType, payload);
            clientConnection.sendMessage(message);
        } catch (SendMessageFailureException e) {
            LOGGER.severe("Failed to send [%s] to client [%s]: %s"
                    .formatted(clientMessageType.getName(), clientConnection, e.getMessage()));
        }
    }

    /**
     * Requests a multi-player game.
     */
    public void requestMultiPlayerGame() {
        sendMessage(ClientMessageType.MULTI_PLAYER_GAME_REQUEST, null);
    }

    /**
     * Requests a single-player game with the specified ai-type.
     * @param aiType The ai-type of the single-player game.
     */
    public void requestSinglePlayerGame(AIType aiType) {
        sendMessage(ClientMessageType.SINGLE_PLAYER_GAME_REQUEST, aiType);
    }

    /**
     * Request a rematch.
     */
    public void requestRematch() {
        sendMessage(ClientMessageType.REMATCH_REQUEST, null);
    }

    /**
     * Sends a move message to the server.
     * @param move The move message to be sent.
     */
    public void sendMoveMessage(Move move) {
        sendMessage(ClientMessageType.MOVE, move);
    }

    /**
     * Sends a chat text to the server.
     * @param chatText The chat text to be sent.
     */
    public void sendChatText(String chatText) {
        sendMessage(ClientMessageType.TEXT, chatText);
    }

    /**
     * Requests a leave-game.
     */
    public void requestLeaveGame() {
        sendMessage(ClientMessageType.LEAVE_GAME_SESSION_REQUEST, null);
    }

    /**
     * Requests a disconnect.
     */
    public void requestDisconnect() {
        sendMessage(ClientMessageType.DISCONNECT_REQUEST, null);
    }
}
