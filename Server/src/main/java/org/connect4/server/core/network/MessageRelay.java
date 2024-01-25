package org.connect4.server.core.network;

import org.connect4.game.networking.messaging.Message;
import org.connect4.server.core.session.GameSession;
import org.connect4.server.logging.ServerLogger;

/**
 * A class that manages the relay of text messages between two clients.
 * @author Hassan
 */
public class MessageRelay implements Runnable {
    private static final ServerLogger LOGGER = ServerLogger.getLogger();

    private final GameSession gameSession;
    private final ClientConnection senderConnection;
    private final ClientConnection receiverConnection;
    private final MessageDispatcher messageDispatcher;

    /**
     * Constructs a new MessageRelay between the specified sender and receiver connections.
     * @param senderConnection The sender connection from which the messages are sent.
     * @param receiverConnection The receiver connection to which the message are received.
     */
    public MessageRelay(GameSession gameSession, ClientConnection senderConnection, ClientConnection receiverConnection) {
        this.gameSession = gameSession;
        this.senderConnection = senderConnection;
        this.receiverConnection = receiverConnection;
        this.messageDispatcher = new MessageDispatcher();
    }

    /**
     * Start relaying text messages between the sender and receiver.
     */
    @Override
    public void run() {
        while (gameSession.isRunning() && senderConnection.isConnected() && receiverConnection.isConnected()) {
            try {
                if (!senderConnection.getTextMessageQueue().isEmpty()) {
                    Message<String> message = senderConnection.getTextMessageQueue().take();
                    messageDispatcher.sendPlayerChat(receiverConnection, message.getPayload());
                }
            } catch (InterruptedException e) {
                LOGGER.severe("Failed to get message from the sender: " + e.getMessage());
            }
        }
    }
}
