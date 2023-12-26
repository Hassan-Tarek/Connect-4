package org.connect4.server.core;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class that manages a game session between two clients.
 * @author Hassan
 */
public class GameSession implements Runnable {
    private final Socket firstClientSocket;
    private final Socket secondClientSocket;
    private final ExecutorService relayExecutor;

    /**
     * Construct a game session between two clients.
     * @param firstClientSocket The first client socket.
     * @param secondClientSocket The second client socket.
     */
    public GameSession(Socket firstClientSocket, Socket secondClientSocket) {
        this.firstClientSocket = firstClientSocket;
        this.secondClientSocket = secondClientSocket;
        this.relayExecutor = Executors.newFixedThreadPool(2);
    }

    /**
     * Starts the game session by setting up the message relays between two client.
     */
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

    /**
     * Shuts down the game session.
     */
    public void shutdown() {
        relayExecutor.shutdownNow();
        closeSocket(firstClientSocket);
        closeSocket(secondClientSocket);
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
