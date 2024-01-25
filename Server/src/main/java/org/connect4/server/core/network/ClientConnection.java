package org.connect4.server.core.network;

import org.connect4.game.logic.core.Move;
import org.connect4.game.networking.messaging.Message;
import org.connect4.game.networking.exceptions.ReceiveMessageFailureException;
import org.connect4.game.networking.exceptions.SendMessageFailureException;
import org.connect4.server.core.ServerManager;
import org.connect4.server.logging.ServerLogger;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A class that manages the communication between the client and the server.
 * @author Hassan
 */
public class ClientConnection implements Comparable<ClientConnection> {
    private static final ServerLogger LOGGER = ServerLogger.getLogger();

    private final Socket clientSocket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final ClientMessageHandler clientMessageHandler;
    private final ClientMessageListener clientMessageListener;
    private final BlockingQueue<Message<Move>> moveMessageQueue;
    private final BlockingQueue<Message<String>> textMessageQueue;
    private final ExecutorService listenerExecutor;

    /**
     * Constructs the client connection.
     * @param clientSocket The socket of the client.
     * @param serverManager The server manager.
     * @throws IOException If an Input/Output error occurs when creating streams.
     */
    public ClientConnection(Socket clientSocket, ServerManager serverManager) throws IOException {
        this.clientSocket = clientSocket;
        this.in = new ObjectInputStream(clientSocket.getInputStream());
        this.out = new ObjectOutputStream(clientSocket.getOutputStream());
        this.clientMessageHandler = new ClientMessageHandler(this, serverManager);
        this.clientMessageListener = new ClientMessageListener(this, clientMessageHandler);
        this.moveMessageQueue = new LinkedBlockingQueue<>();
        this.textMessageQueue = new LinkedBlockingQueue<>();
        this.listenerExecutor = Executors.newSingleThreadExecutor();
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
     */
    public void startMessageListener() {
        listenerExecutor.submit(clientMessageListener);
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
            LOGGER.severe(errorMessage);
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
            LOGGER.severe(errorMessage);
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
            shutdownListenerExecutor();
            clientMessageHandler.shutdown();
            if (this.isConnected()) {
                clientSocket.close();
                closeStreams();
            }

            LOGGER.info("Client disconnected.");
        } catch (IOException e) {
            LOGGER.severe("Failed to close client socket: " + e.getMessage());
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
            LOGGER.severe("Failed to close streams: " + e.getMessage());
        }
    }

    /**
     * Shutdown the listener executor.
     */
    private void shutdownListenerExecutor() {
        try {
            if (!listenerExecutor.isShutdown()) {
                listenerExecutor.shutdown();

                if (!listenerExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    listenerExecutor.shutdownNow();
                }
            }

            LOGGER.info("Listener Executor shut down successfully.");
        } catch (InterruptedException e) {
            LOGGER.severe("Failed to shutdown the listener executor: " + e.getMessage());
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
