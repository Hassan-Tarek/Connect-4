package org.connect4.server.core;

import org.connect4.game.networking.Message;
import org.connect4.game.networking.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameSessionManager implements Runnable {
    private final Socket firstClientSocket;
    private final Socket secondClientSocket;

    public GameSessionManager(Socket firstClientSocket, Socket secondClientSocket) {
        this.firstClientSocket = firstClientSocket;
        this.secondClientSocket = secondClientSocket;
    }

    @Override
    public void run() {
        try (ObjectInputStream firstClientInputStream = new ObjectInputStream(firstClientSocket.getInputStream());
             ObjectOutputStream firstClientOutputStream = new ObjectOutputStream(firstClientSocket.getOutputStream());
             ObjectInputStream secondClientInputStream = new ObjectInputStream(secondClientSocket.getInputStream());
             ObjectOutputStream secondClientOutputStream = new ObjectOutputStream(secondClientSocket.getOutputStream())) {
            // Inform two clients about each other
            Message<?> message = new Message<>(MessageType.TEXT, "Opponent connected!");
            firstClientOutputStream.writeObject(message);
            firstClientOutputStream.flush();
            secondClientOutputStream.writeObject(message);
            secondClientOutputStream.flush();

            // Message relay between two clients
            Thread firstMessageRelayThread = new Thread(new MessageRelay(firstClientInputStream, secondClientOutputStream));
            Thread secondMessageRelayThread = new Thread(new MessageRelay(secondClientInputStream, firstClientOutputStream));
            firstMessageRelayThread.start();
            secondMessageRelayThread.start();

            // Wait for message relay threads to finish
            firstMessageRelayThread.join();
            secondMessageRelayThread.join();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
