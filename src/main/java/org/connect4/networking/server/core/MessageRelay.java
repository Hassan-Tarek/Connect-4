package org.connect4.networking.server.core;

import org.connect4.networking.shared.Message;
import org.connect4.networking.shared.exceptions.ReceiveMessageFailureException;
import org.connect4.networking.shared.exceptions.SendMessageFailureException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MessageRelay implements Runnable {
    private final ObjectInputStream senderInputStream;
    private final ObjectOutputStream receiverOutputStream;

    public MessageRelay(ObjectInputStream senderInputStream, ObjectOutputStream receiverOutputStream) {
        this.senderInputStream = senderInputStream;
        this.receiverOutputStream = receiverOutputStream;
    }

    @Override
    public void run() {
        relayMessages();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void relayMessages() {
        while (true) {
            try {
                Message<?> message = getReceiveMessage();
                sendMessage(message);
            } catch (ReceiveMessageFailureException | SendMessageFailureException e) {
                System.err.println("Message relay failed: " + e.getMessage());
            }
        }
    }

    private Message<?> getReceiveMessage() throws ReceiveMessageFailureException {
        try {
            return (Message<?>) senderInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new ReceiveMessageFailureException("Failed to deserialize message: " + e.getMessage());
        } catch (IOException e) {
            throw new ReceiveMessageFailureException("Failed to receive message from a client: " + e.getMessage());
        }
    }

    private void sendMessage(Message<?> message) throws SendMessageFailureException {
        try {
            receiverOutputStream.writeObject(message);
            receiverOutputStream.flush();
        } catch (IOException e) {
            throw new SendMessageFailureException("Failed to send message to a client: " + e.getMessage());
        }
    }
}
