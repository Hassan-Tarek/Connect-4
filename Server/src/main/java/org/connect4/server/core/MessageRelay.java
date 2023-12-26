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
    private final Socket senderSocket;
    private final Socket receiverSocket;

    /**
     * Constructs a new MessageRelay between the specified sender and receiver sockets.
     * @param senderSocket The socket from which the messages are sent.
     * @param receiverSocket The socket to which the message are received.
     */
    public MessageRelay(Socket senderSocket, Socket receiverSocket) {
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
        ObjectInputStream input = null;
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(receiverSocket.getOutputStream());
            input = new ObjectInputStream(senderSocket.getInputStream());

            while (!Thread.currentThread().isInterrupted()) {
                Message<?> message = getReceiveMessage(input);
                if (message == null) {
                    break;
                }
                sendMessage(output, message);
            }

        } catch (ReceiveMessageFailureException | SendMessageFailureException e) {
            if (!senderSocket.isClosed() && !receiverSocket.isClosed()) {
                System.err.println("ERROR: Message relay failed: " + e.getMessage());
            }
        } catch (IOException e) {
            if (!senderSocket.isClosed() && !receiverSocket.isClosed()) {
                System.err.println("ERROR: Failed to open input/output streams: " + e.getMessage());
            }
        } finally {
            closeStreams(input, output);
            closeSocket(senderSocket);
            closeSocket(receiverSocket);
        }
    }

    /**
     * Receives a message from sender's input stream.
     * @param input The input stream to read message from.
     * @return The received message.
     * @throws ReceiveMessageFailureException If failed to receive message.
     */
    private Message<?> getReceiveMessage(ObjectInputStream input) throws ReceiveMessageFailureException {
        try {
            return (Message<?>) input.readObject();
        } catch (EOFException e) {
            return null;
        } catch (ClassNotFoundException | IOException e) {
            throw new ReceiveMessageFailureException("Failed to receive message from a client: " + e.getMessage());
        }
    }

    /**
     * Sends a message to the receiver's output stream.
     * @param output The output stream to write message to.
     * @param message The message to be sent.
     * @throws SendMessageFailureException If failed to send message.
     */
    private void sendMessage(ObjectOutputStream output, Message<?> message) throws SendMessageFailureException {
        try {
            output.writeObject(message);
            output.flush();
        } catch (IOException e) {
            throw new SendMessageFailureException("Failed to send message to a client: " + e.getMessage());
        }
    }

    /**
     * Closes the streams
     * @param streams The streams to be closed.
     */
    private void closeStreams(Closeable... streams) {
        for (Closeable stream : streams) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    System.err.println("ERROR: Failed to close stream: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Closes a socket.
     * @param socket The socket to be closed.
     */
    private void closeSocket(Socket socket) {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("ERROR: Failed to close client socket: " + e.getMessage());
        }
    }
}
