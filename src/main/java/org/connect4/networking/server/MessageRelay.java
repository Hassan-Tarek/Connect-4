package org.connect4.networking.server;

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

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        while (true) {
            Object message = getReceivedMessage();
            sendMessage(message);
        }
    }

    private Object getReceivedMessage() {
        try {
            return senderInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(Object message) {
        try {
            receiverOutputStream.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}