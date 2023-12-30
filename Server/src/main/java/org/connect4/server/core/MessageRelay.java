package org.connect4.server.core;

import org.connect4.game.networking.Message;
import org.connect4.game.networking.exceptions.ReceiveMessageFailureException;
import org.connect4.game.networking.exceptions.SendMessageFailureException;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * A class that manages the relay of messages between two clients.
 * @author Hassan
 */
public class MessageRelay implements Runnable {
    private final ServerManager serverManager;
    private final Socket senderSocket;
    private final Socket receiverSocket;

    /**
     * Constructs a new MessageRelay between the specified sender and receiver sockets.
     * @param serverManager The server manager.
     * @param senderSocket The socket from which the messages are sent.
     * @param receiverSocket The socket to which the message are received.
     */
    public MessageRelay(ServerManager serverManager, Socket senderSocket, Socket receiverSocket) {
        this.serverManager = serverManager;
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
    private void relayMessages() {
        while (serverManager.isRunning()) {
            try {
                Message<?> message = serverManager.receiveMessage(senderSocket);
                if (message == null) {
                    break;
                }
                serverManager.sendMessage(receiverSocket, message);
            } catch (SendMessageFailureException | ReceiveMessageFailureException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
