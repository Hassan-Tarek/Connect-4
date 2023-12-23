package org.connect4.server.core;

import org.connect4.server.exceptions.ServerStartFailureException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerManager implements Runnable {
    private final ServerSocket serverSocket;
    private final List<Socket> waitingSockets;
    private final List<GameSession> gameSessions;
    private final ExecutorService executorService;
    private final AtomicBoolean running;

    public ServerManager(int port) throws ServerStartFailureException {
        try {
            this.serverSocket = new ServerSocket(port);
            this.waitingSockets = new ArrayList<>();
            this.gameSessions = new ArrayList<>();
            this.executorService = Executors.newCachedThreadPool();
            this.running = new AtomicBoolean(false);
        } catch (IOException e) {
            throw new ServerStartFailureException("Failed to bind the server to port %d: ".formatted(port) + e.getMessage());
        }
    }

    public List<GameSession> getGameSessions() {
        return gameSessions;
    }

    public List<Socket> getWaitingSockets() {
        return waitingSockets;
    }

    @Override
    public void run() {
        try {
            System.out.println("Server started...");

            while (running.get()) {
                Socket acceptedClientSocket = serverSocket.accept();
                waitingSockets.add(acceptedClientSocket);
                System.out.println("New client accepted!");


                if (waitingSockets.size() >= 2) {
                    Socket firstClientSocket = waitingSockets.remove(0);
                    Socket secondClientSocket = waitingSockets.remove(0);

                    // Start a new game session for these two clients
                    GameSession gameSession = new GameSession(firstClientSocket, secondClientSocket);
                    gameSessions.add(gameSession);
                    executorService.submit(gameSession);
                }
            }
        } catch (IOException e) {
            if (!serverSocket.isClosed()) {
                System.err.println("Server can't accept connection anymore: " + e.getMessage());
            }
        }
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            Thread serverThread = new Thread(this);
            serverThread.start();
        }
    }

    public void shutdown() {
        if (running.compareAndSet(true, false)) {
            try {
                System.out.println("Server stopped...");
                executorService.shutdownNow();
                serverSocket.close();

                for (Socket socket : waitingSockets) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.err.println("Failed to close waiting client socket: " + e.getMessage());
                    }
                }

                for (GameSession session : gameSessions) {
                    session.shutdown();
                }
            } catch (IOException e) {
                System.err.println("Failed to close server: " + e.getMessage());
            }
        }
    }
}
