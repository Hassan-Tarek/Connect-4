package org.connect4.server.core;

import org.connect4.game.logic.core.Move;
import org.connect4.game.networking.Message;
import org.connect4.game.networking.exceptions.ReceiveMessageFailureException;
import org.connect4.game.networking.exceptions.SendMessageFailureException;
import org.connect4.server.logging.ServerLogger;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A class that manages the communication between the client and the server.
 * @author Hassan
 */
public class ClientConnection implements Comparable<ClientConnection> {
    private static final ServerLogger logger = ServerLogger.getLogger();

    private final Socket clientSocket;
    private final BlockingQueue<Message<Move>> moveMessageQueue;
    private final BlockingQueue<Message<String>> textMessageQueue;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    /**
     * Constructs the client connection.
     * @param clientSocket The socket of the client.
     * @throws IOException If an Input/Output error occurs when creating streams.
     */
    public ClientConnection(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.moveMessageQueue = new LinkedBlockingQueue<>();
        this.textMessageQueue = new LinkedBlockingQueue<>();
        this.in = new ObjectInputStream(clientSocket.getInputStream());
        this.out = new ObjectOutputStream(clientSocket.getOutputStream());
    }

    /**
     * Gets the client connection socket.
     * @return The client connection socket.
     */
    public Socket getClientSocket() {
        return clientSocket;
    }

    /**
     * Gets the move message queue of this client connection.
     * @return The move message queue.
     */
    public BlockingQueue<Message<Move>> getMoveMessageQueue() {
        return moveMessageQueue;
    }

    /**
     * Gets the text message queue of this client connection.
     * @return The text message queue.
     */
    public BlockingQueue<Message<String>> getTextMessageQueue() {
        return textMessageQueue;
    }

    /**
     * Starts the message listener to receive message from this client connection.
     * @param serverManager The server manager.
     */
    public void startListening(ServerManager serverManager) {
        MessageListener messageListener = new MessageListener(this, serverManager);
        Thread messageListenerThread = new Thread(messageListener);
        messageListenerThread.start();
    }

    /**
     * Sends a message to the receiver's output stream.
     * @param message The message to be sent.
     * @throws SendMessageFailureException If failed to send the message.
     */
    public void sendMessage(Message<?> message) throws SendMessageFailureException {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            String errorMessage = "Failed to send message to a client: " + e.getMessage();
            logger.severe(errorMessage);
            throw new SendMessageFailureException(errorMessage);
        }
    }

    /**
     * Receives a message from sender's input stream.
     * @return The received message.
     * @throws ReceiveMessageFailureException If failed to receive the message.
     */
    public Message<?> receiveMessage() throws ReceiveMessageFailureException {
        try {
            return (Message<?>) in.readObject();
        } catch (EOFException e) {
            return null;
        } catch (ClassNotFoundException | IOException e) {
            String errorMessage = "Failed to receive message from a client: " + e.getMessage();
            logger.severe(errorMessage);
            throw new ReceiveMessageFailureException(errorMessage);
        }
    }

    /**
     * Checks whether this client is connected to the server or not.
     * @return True if this client is connected to the server, False otherwise.
     */
    public boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
    }

    /**
     * Disconnects the client connection.
     */
    public void disconnect() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                logger.info("Client disconnected.");
            }
            closeStreams();
        } catch (IOException e) {
            logger.severe("Failed to close client socket: " + e.getMessage());
        }
    }

    /**
     * Closes Input/Output streams of this client connection.
     */
    private void closeStreams() {
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            logger.severe("Failed to close streams: " + e.getMessage());
        }
    }

    /**
     * Compares this client connection to another client connection based on the remote socket address.
     * @param otherClient The other client connection to compare with.
     * @return A negative, positive integers or zero.
     */
    @Override
    public int compareTo(ClientConnection otherClient) {
        InetSocketAddress thisClientAddress = (InetSocketAddress) this.clientSocket.getRemoteSocketAddress();
        InetSocketAddress otherClientAddress = (InetSocketAddress) otherClient.clientSocket.getRemoteSocketAddress();
        return thisClientAddress.toString().compareTo(otherClientAddress.toString());
    }

    /**
     * Compares this client connection with another object for equality.
     * @param obj The object to compare with.
     * @return true if this client connection is equal to the other object, false otherwise.
     */
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ClientConnection that = (ClientConnection) obj;
        return clientSocket.equals(that.clientSocket);
    }

    /**
     * Gets a hash code for this client connection.
     * @return A hash code for this client connection.
     */
    @Override
    public int hashCode() {
        return clientSocket.hashCode();
    }
}
