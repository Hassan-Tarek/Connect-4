package org.connect4.server.core;

import org.connect4.server.logging.ServerLogger;
import org.connect4.game.logic.core.Move;
import org.connect4.game.networking.Message;
import org.connect4.game.networking.MessageType;
import org.connect4.game.networking.exceptions.SendMessageFailureException;

import java.util.concurrent.BlockingQueue;

/**
 * A class that manages the relay of messages between two clients.
 * @author Hassan
 */
public class MessageRelay implements Runnable {
    private static final ServerLogger logger = ServerLogger.getLogger();

    private final ClientConnection senderConnection;
    private final ClientConnection receiverConnection;
    private final BlockingQueue<Message<Move>> moveMessageQueue;

    /**
     * Constructs a new MessageRelay between the specified sender and receiver sockets.
     * @param senderConnection The sender connection from which the messages are sent.
     * @param receiverConnection The receiver connection to which the message are received.
     * @param moveMessageQueue The move message queue.
     */
    public MessageRelay(ClientConnection senderConnection, ClientConnection receiverConnection,
                        BlockingQueue<Message<Move>> moveMessageQueue) {
        this.senderConnection = senderConnection;
        this.receiverConnection = receiverConnection;
        this.moveMessageQueue = moveMessageQueue;
    }

    /**
     * Start relaying messages between the sender and receiver.
     */
    @Override
    public void run() {
        relayMessages();
    }

    /**
     * Relays messages between the sender and receiver.
     */
    @SuppressWarnings("unchecked")
    private void relayMessages() {
        while (senderConnection.isConnected() && receiverConnection.isConnected()) {
            try {
                Message<?> message = senderConnection.getMessageQueue().take();

                if (message.getType() == MessageType.MOVE) {
                    moveMessageQueue.add((Message<Move>) message);
                } else {
                    receiverConnection.sendMessage(message);
                }
            } catch (SendMessageFailureException e) {
                logger.severe("Failed to send message to the receiver: " + e.getMessage());
            } catch (InterruptedException e) {
                logger.severe("Failed to get message from the sender: " + e.getMessage());
            }
        }
    }
}
