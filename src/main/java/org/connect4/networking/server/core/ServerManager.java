package org.connect4.networking.server.core;

import org.connect4.networking.server.exceptions.ServerStartFailureException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerManager {
    private final int port;
    private final List<Socket> sockets;
    private final ExecutorService executorService;

    public ServerManager(int port) {
        this.port = port;
        this.sockets = new ArrayList<>();
        this.executorService = Executors.newCachedThreadPool();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void start() throws ServerStartFailureException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started...");

            while (true) {
                Socket acceptedClientSocket = serverSocket.accept();
                System.out.println("New client accepted!");

                if (!sockets.isEmpty()) {
                    Socket matchedClientSocket = sockets.remove(0);

                    // Start a new game session for these two clients
                    GameSessionManager gameSessionManager = new GameSessionManager(acceptedClientSocket, matchedClientSocket);
                    executorService.submit(gameSessionManager);
                } else {
                    sockets.add(acceptedClientSocket);
                }
            }
        } catch (IOException e) {
            throw new ServerStartFailureException("Failed to bind the server to port %d: ".formatted(port) + e.getMessage());
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
