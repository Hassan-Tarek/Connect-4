package org.connect4.client.core;

import org.connect4.client.exceptions.ServerConnectionFailureException;
import org.connect4.client.logger.ClientLogger;
import org.connect4.game.networking.exceptions.ReceiveMessageFailureException;
import org.connect4.game.networking.exceptions.SendMessageFailureException;
import org.connect4.game.networking.messaging.Message;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * A class that manages the communication between the client and the server.
 * @author Hassan
 * */
public class ClientConnection {
    private static final ClientLogger LOGGER = ClientLogger.getLogger();

    private final String serverAddress;
    private final int serverPort;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;


    /**
     * Constructs the client connection.
     */
    public ClientConnection(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    /**
     * Connects to the server.
     * @throws ServerConnectionFailureException If failed to connect to the server.
     */
    public void connectToServer() throws ServerConnectionFailureException {
        try {
            this.socket = new Socket(serverAddress, serverPort);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());

            LOGGER.info("Successfully connected to server: " + serverAddress + ":" + serverPort);
        } catch (IOException e) {
            String message = "Failed to connect to the server with address: %s, port: %d".formatted(serverAddress, serverPort);
            LOGGER.severe(message);
            throw new ServerConnectionFailureException(message);
        }
    }

    /**
     * Sends a message to the output stream.
     * @param message The message to be sent.
     * @throws SendMessageFailureException If failed to send the message.
     */
    public void sendMessage(Message<?> message) throws SendMessageFailureException {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            String errorMessage = "Failed to send message to the server: " + e.getMessage();
            LOGGER.severe(errorMessage);
            throw new SendMessageFailureException(errorMessage);
        }
    }

    /**
     * Receives a message from input stream.
     * @return The received message.
     * @throws ReceiveMessageFailureException If failed to receive the message.
     */
    public Message<?> receiveMessage() throws ReceiveMessageFailureException {
        try {
            return (Message<?>) in.readObject();
        } catch (EOFException e) {
            return null;
        } catch (ClassNotFoundException | IOException e) {
            String errorMessage = "Failed to receive message from the server: " + e.getMessage();
            LOGGER.severe(errorMessage);
            throw new ReceiveMessageFailureException(errorMessage);
        }
    }

    /**
     * Checks whether this client is connected to the server or not.
     * @return True if this client is connected to the server, False otherwise.
     */
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    /**
     * Disconnects the client connection.
     */
    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                LOGGER.info("Client disconnected.");
            }
            closeStreams();
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
}
