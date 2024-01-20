package org.connect4.client.core;

import org.connect4.game.logic.core.Move;
import org.connect4.game.networking.messaging.Message;
import org.connect4.game.networking.messaging.ServerMessageType;

public class MessageHandler {
    @SuppressWarnings("unchecked")
    public static <T> void handleMessage(Message<T> message) {
        ServerMessageType serverMessageType = message.getType();
        switch (serverMessageType) {
            case MOVE -> handleMoveMessage((Message<Move>) message);
            case TEXT -> handleTextMessage((Message<String>) message);
            default -> System.err.println("ERROR: Could not process the message.");
        }
    }

    private static void handleMoveMessage(Message<Move> moveMessage) {
        Move move = moveMessage.getPayload();
        System.out.println("Opponent's Move at column: " + move.getColumn());
    }

    private static void handleTextMessage(Message<String> textMessage) {
        String text = textMessage.getPayload();
        System.out.println("Opponent's Text: " + text);
    }
}
