package org.connect4.server.core;

import org.connect4.server.logging.ServerLogger;
import org.connect4.game.logic.core.Move;
import org.connect4.game.networking.Message;
import org.connect4.game.networking.MessageType;
import org.connect4.game.networking.exceptions.ReceiveMessageFailureException;
import org.connect4.game.networking.exceptions.SendMessageFailureException;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * A class that manages the relay of messages between two clients.
 * @author Hassan
 */
public class MessageRelay implements Runnable {
    private static final ServerLogger logger = ServerLogger.getLogger();

    private final ServerManager serverManager;
    private final BlockingQueue<Message<Move>> moveMessageQueue;
    private final Socket senderSocket;
    private final Socket receiverSocket;

    /**
     * Constructs a new MessageRelay between the specified sender and receiver sockets.
     * @param serverManager The server manager.
     * @param moveMessageQueue The move message queue.
     * @param senderSocket The socket from which the messages are sent.
     * @param receiverSocket The socket to which the message are received.
     */
    public MessageRelay(ServerManager serverManager, BlockingQueue<Message<Move>> moveMessageQueue,
                        Socket senderSocket, Socket receiverSocket) {
        this.serverManager = serverManager;
        this.moveMessageQueue = moveMessageQueue;
        this.senderSocket = senderSocket;
        this.receiverSocket = receiverSocket;
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
        while (serverManager.isRunning()) {
            try {
                Message<?> message = serverManager.receiveMessage(senderSocket);

                if (message != null && message.getType() == MessageType.MOVE) {
                    moveMessageQueue.add((Message<Move>) message);
                } else {
                    serverManager.sendMessage(receiverSocket, message);
                }
            } catch (SendMessageFailureException | ReceiveMessageFailureException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
