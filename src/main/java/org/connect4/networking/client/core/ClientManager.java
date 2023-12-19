package org.connect4.networking.client.core;

import org.connect4.networking.client.exceptions.ServerConnectionFailureException;
import org.connect4.networking.shared.Message;
import org.connect4.networking.shared.exceptions.ReceiveMessageFailureException;
import org.connect4.networking.shared.exceptions.SendMessageFailureException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientManager {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public ClientManager(String serverAddress, int port) throws ServerConnectionFailureException {
        try {
            this.socket = new Socket(serverAddress, port);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new ServerConnectionFailureException("Failed to connect to a server: " + e.getMessage());
        }
    }

    public void sendMessage(Message<?> message) throws SendMessageFailureException {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            throw new SendMessageFailureException("Failed to send message to the server: " + e.getMessage());
        }
    }

    public Message<?> getReceivedMessage() throws ReceiveMessageFailureException {
        try {
            System.out.println("Waiting to receive message...");
            Message<?> receivedMessage = (Message<?>) in.readObject();
            System.out.println("Received message: " + receivedMessage);
            return receivedMessage;
        } catch (IOException | ClassNotFoundException e) {
            throw new ReceiveMessageFailureException("Failed to fetch message from the server: " + e.getMessage());
        }
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Failed to close client socket: " + e.getMessage());
        }
    }
}
