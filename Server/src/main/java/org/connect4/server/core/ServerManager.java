package org.connect4.server.core;

import org.connect4.server.core.network.ClientConnection;
import org.connect4.server.core.network.MessageDispatcher;
import org.connect4.server.exceptions.ServerStartFailureException;
import org.connect4.server.logging.ServerLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class that represents the server for Connect-4 Game.
 * @author Hassan
 */
public class ServerManager implements Runnable {
    private static final ServerLogger LOGGER = ServerLogger.getLogger();

    private final int port;
    private final ClientManager clientManager;
    private final GameSessionManager gameSessionManager;
    private final MessageDispatcher messageDispatcher;
    private final AtomicBoolean running;

    private ServerSocket serverSocket;

    /**
     * Constructs a new ServerManager with the specified port number.
     * @param port The port number to bind the server to.
     */
    public ServerManager(int port) {
        this.port = port;
        this.clientManager = new ClientManager();
        this.gameSessionManager = new GameSessionManager();
        this.messageDispatcher = new MessageDispatcher();
        this.running = new AtomicBoolean(false);
    }

    /**
     * Gets client manager.
     * @return The client manager.
     */
    public ClientManager getClientManager() {
        return clientManager;
    }

    /**
     * Gets game session manager.
     * @return The game session manager.
     */
    public GameSessionManager getGameSessionManager() {
        return gameSessionManager;
    }

    /**
     * Starts running the server.
     */
    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(port);
            LOGGER.info("Listening for client requests...");

            while (running.get()) {
                try {
                    Socket acceptedClientSocket = serverSocket.accept();
                    LOGGER.info("client has been accepted with address: " + acceptedClientSocket.getRemoteSocketAddress());

                    ClientConnection clientConnection = new ClientConnection(acceptedClientSocket, this);
                    clientConnection.startMessageListener();
                    clientManager.addConnectedClient(clientConnection);

                    LOGGER.fine("New client with address: %s has been accepted.".formatted(acceptedClientSocket.getRemoteSocketAddress()));
                } catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        LOGGER.severe("Server can't accept connection anymore: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            String message = "Failed to bind the server to port %d: ".formatted(port) + e.getMessage();
            LOGGER.severe(message);
            throw new RuntimeException(new ServerStartFailureException(message));
        } finally {
            shutdown();
        }
    }

    /**
     * Starts the server.
     */
    public void start() {
        if (running.compareAndSet(false, true)) {
            Thread serverThread = new Thread(this);
            serverThread.start();
            LOGGER.info("Server started!");
        }
    }

    /**
     * shutdown the server.
     */
    public void shutdown() {
        if (running.compareAndSet(true, false)) {
            try {
                messageDispatcher.broadcastServerStopped(clientManager.getConnectedClients().stream().toList());
                clientManager.disconnectClients();
                gameSessionManager.shutdown();

                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }

                LOGGER.info("Server stopped.");
            } catch (IOException e) {
                LOGGER.severe("Failed to shutdown the server: " + e.getMessage());
            } finally {
                clientManager.cleanup();
                gameSessionManager.cleanup();
            }
        }
    }
}
