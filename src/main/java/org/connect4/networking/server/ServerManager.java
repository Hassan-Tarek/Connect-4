package org.connect4.networking.server;

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
    public void start() {
        while (true) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                Socket incoming = serverSocket.accept();
                if (!sockets.isEmpty()) {
                    Socket matchedClient = sockets.remove(0);

                    // Start a new game session for these two clients
                    GameSessionManager gameSessionManager = new GameSessionManager(incoming, matchedClient);
                    executorService.submit(gameSessionManager);
                } else {
                    sockets.add(incoming);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void shutdown() {
        executorService.shutdownNow();
    }
}
