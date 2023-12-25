package org.connect4.server.core;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameSession implements Runnable {
    private final Socket firstClientSocket;
    private final Socket secondClientSocket;
    private final ExecutorService relayExecutor;

    public GameSession(Socket firstClientSocket, Socket secondClientSocket) {
        this.firstClientSocket = firstClientSocket;
        this.secondClientSocket = secondClientSocket;
        this.relayExecutor = Executors.newFixedThreadPool(2);
    }

    @Override
    public void run() {
        try {
            relayExecutor.submit(new MessageRelay(firstClientSocket, secondClientSocket));
            relayExecutor.submit(new MessageRelay(secondClientSocket, firstClientSocket));
        } catch (Exception e) {
            System.err.println("ERROR: Failed to start message relay: " + e.getMessage());
        } finally {
            relayExecutor.shutdown();
        }
    }

    public void shutdown() {
        relayExecutor.shutdownNow();
        closeSocket(firstClientSocket);
        closeSocket(secondClientSocket);
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