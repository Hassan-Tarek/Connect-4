package org.connect4.server.core;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class that manages a game session between two clients.
 * @author Hassan
 */
public class GameSession implements Runnable {
    private final ServerManager serverManager;
    private final Socket firstClientSocket;
    private final Socket secondClientSocket;
    private final ExecutorService relayExecutor;

    /**
     * Construct a game session between two clients.
     * @param serverManager The server manager.
     * @param firstClientSocket The first client socket.
     * @param secondClientSocket The second client socket.
     */
    public GameSession(ServerManager serverManager, Socket firstClientSocket, Socket secondClientSocket) {
        this.serverManager = serverManager;
        this.firstClientSocket = firstClientSocket;
        this.secondClientSocket = secondClientSocket;
        this.relayExecutor = Executors.newFixedThreadPool(2);
    }

    /**
     * Starts the game session by setting up the message relays between two clients.
     */
    @Override
    public void run() {
        try {
            // Start message relays
            relayExecutor.submit(new MessageRelay(serverManager, firstClientSocket, secondClientSocket));
            relayExecutor.submit(new MessageRelay(serverManager, secondClientSocket, firstClientSocket));
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
        serverManager.closeSocket(firstClientSocket);
        serverManager.closeSocket(secondClientSocket);
    }
}
