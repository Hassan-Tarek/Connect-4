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

public class MessageRelay implements Runnable {
    private final Socket senderSocket;
    private final Socket receiverSocket;

    public MessageRelay(Socket senderSocket, Socket receiverSocket) {
        this.senderSocket = senderSocket;
        this.receiverSocket = receiverSocket;
    }

    @Override
    public void run() {
        relayMessages();
    }

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

    private Message<?> getReceiveMessage(ObjectInputStream input) throws ReceiveMessageFailureException {
        try {
            return (Message<?>) input.readObject();
        } catch (EOFException e) {
            return null;
        } catch (ClassNotFoundException | IOException e) {
            throw new ReceiveMessageFailureException("Failed to receive message from a client: " + e.getMessage());
        }
    }

    private void sendMessage(ObjectOutputStream output, Message<?> message) throws SendMessageFailureException {
        try {
            output.writeObject(message);
            output.flush();
        } catch (IOException e) {
            throw new SendMessageFailureException("Failed to send message to a client: " + e.getMessage());
        }
    }

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
