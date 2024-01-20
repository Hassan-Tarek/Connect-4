package org.connect4.server.core.network;

import org.connect4.game.networking.messaging.Message;
import org.connect4.game.networking.exceptions.SendMessageFailureException;
import org.connect4.server.logging.ServerLogger;

/**
 * A class that manages the relay of text messages between two clients.
 * @author Hassan
 */
public class MessageRelay implements Runnable {
    private static final ServerLogger logger = ServerLogger.getLogger();

    private final ClientConnection senderConnection;
    private final ClientConnection receiverConnection;

    /**
     * Constructs a new MessageRelay between the specified sender and receiver connections.
     * @param senderConnection The sender connection from which the messages are sent.
     * @param receiverConnection The receiver connection to which the message are received.
     */
    public MessageRelay(ClientConnection senderConnection, ClientConnection receiverConnection) {
        this.senderConnection = senderConnection;
        this.receiverConnection = receiverConnection;
    }

    /**
     * Start relaying text messages between the sender and receiver.
     */
    @Override
    public void run() {
        relayMessages();
    }

    /**
     * Relays text messages between the sender and receiver.
     */
    private void relayMessages() {
        while (senderConnection.isConnected() && receiverConnection.isConnected()) {
            try {
                Message<String> message = senderConnection.getTextMessageQueue().take();
                receiverConnection.sendMessage(message);
            } catch (SendMessageFailureException e) {
                logger.severe("Failed to send message to the receiver: " + e.getMessage());
            } catch (InterruptedException e) {
                logger.severe("Failed to get message from the sender: " + e.getMessage());
            }
        }
    }
}
