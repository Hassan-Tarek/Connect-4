package org.connect4.server.core;

import org.connect4.game.ai.enums.AIType;
import org.connect4.game.networking.Message;
import org.connect4.game.networking.MessageType;
import org.connect4.server.core.session.GameSession;
import org.connect4.server.core.session.MultiPlayerGameSession;
import org.connect4.server.core.session.SinglePlayerGameSession;
import org.connect4.server.exceptions.ServerStartFailureException;
import org.connect4.server.logging.ServerLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class that represents the server for Connect-4 Game.
 * @author Hassan
 */
public class ServerManager implements Runnable {
    private static final ServerLogger logger = ServerLogger.getLogger();

    private final int port;
    private final Set<ClientConnection> waitingClientConnections;
    private final Set<ClientConnection> allAvailableClientConnections;
    private final List<GameSession> multiPlayerGameSessions;
    private final List<GameSession> singlePlayerGameSessions;
    private final AtomicBoolean running;

    private ServerSocket serverSocket;
    private ExecutorService executorService;

    /**
     * Constructs a new ServerManager with the specified port number.
     * @param port The port number to bind the server to.
     */
    public ServerManager(int port) {
        this.port = port;
        this.waitingClientConnections = new ConcurrentSkipListSet<>();
        this.allAvailableClientConnections = new ConcurrentSkipListSet<>();
        this.multiPlayerGameSessions = new CopyOnWriteArrayList<>();
        this.singlePlayerGameSessions = new CopyOnWriteArrayList<>();
        this.running = new AtomicBoolean(false);
    }

    /**
     * Gets the list of waiting client connections.
     * @return The list of waiting client connections.
     */
    public Set<ClientConnection> getWaitingClientConnections() {
        return waitingClientConnections;
    }

    /**
     * Gets the list of multi-player game sessions.
     * @return The list of multi-player game sessions.
     */
    public List<GameSession> getMultiPlayerGameSessions() {
        return multiPlayerGameSessions;
    }

    /**
     * Gets the list of single-player game sessions.
     * @return The list of single-player game sessions.
     */
    public List<GameSession> getSinglePlayerGameSessions() {
        return singlePlayerGameSessions;
    }

    /**
     * Starts running the server.
     */
    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(port);
            this.executorService = Executors.newCachedThreadPool();
            logger.info("Listening for client requests...");

            while (running.get()) {
                try {
                    Socket acceptedClientSocket = serverSocket.accept();
                    logger.info("client has been accepted!");

                    ClientConnection clientConnection = new ClientConnection(acceptedClientSocket);
                    clientConnection.startListening(this);

                    allAvailableClientConnections.add(clientConnection);
                    logger.fine("New client with address: %s has been accepted.".formatted(acceptedClientSocket.getRemoteSocketAddress()));
                } catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        logger.severe("Server can't accept connection anymore: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            String message = "Failed to bind the server to port %d: ".formatted(port) + e.getMessage();
            logger.severe(message);
            throw new RuntimeException(new ServerStartFailureException(message));
        } finally {
            shutdown();
        }
    }

    /**
     * Handles the multi-player game session by matching the client with another waiting client.
     * @param clientConnection The client that request multi-player game session.
     */
    public void handleMultiPlayerGameSession(ClientConnection clientConnection) {
        Iterator<ClientConnection> clientConnectionIterator = waitingClientConnections.iterator();

        if (clientConnectionIterator.hasNext()) {
            // Retrieve the opponent connection and remove it from the set
            ClientConnection opponentClientConnection = clientConnectionIterator.next();
            clientConnectionIterator.remove();

            if (clientConnection != opponentClientConnection) {
                // Start a new game session
                GameSession gameSession = new MultiPlayerGameSession(clientConnection, opponentClientConnection);
                addGameSession(gameSession);
            }
        } else {
            waitingClientConnections.add(clientConnection);
        }
    }

    /**
     * Handles the single-player game session by receiving an AI type and starts a game session with that AI.
     * @param clientConnection The client that request single-player game session.
     * @param aiType The selected AI type.
     */
    public void handleSinglePlayerGameSession(ClientConnection clientConnection, AIType aiType) {
        // Start a new game session
        GameSession gameSession = new SinglePlayerGameSession(clientConnection, aiType);
        addGameSession(gameSession);
    }

    /**
     * Handles the client disconnection request.
     * @param clientConnection The client connection
     */
    public void handleClientDisconnection(ClientConnection clientConnection) {
        clientConnection.disconnect();
        allAvailableClientConnections.remove(clientConnection);
        waitingClientConnections.remove(clientConnection);
        logger.info("Client with address: %s has been disconnected.".formatted(clientConnection.getClientSocket().getRemoteSocketAddress()));
    }

    /**
     * Adds the given game session to the list of sessions and submits it to the executor service for execution.
     * @param gameSession The game session to be added and executed.
     */
    private void addGameSession(GameSession gameSession) {
        if (gameSession instanceof MultiPlayerGameSession) {
            multiPlayerGameSessions.add(gameSession);
        } else if (gameSession instanceof SinglePlayerGameSession) {
            singlePlayerGameSessions.add(gameSession);
        }
        executorService.submit(gameSession);

        logger.info("New GameSession started.");
    }

    /**
     * Starts the server.
     */
    public void start() {
        if (running.compareAndSet(false, true)) {
            Thread serverThread = new Thread(this);
            serverThread.start();
            logger.info("Server started...");
        }
    }

    /**
     * Stops the server.
     */
    public void shutdown() {
        if (running.compareAndSet(true, false)) {
            try {
                for (ClientConnection clientConnection : allAvailableClientConnections) {
                    clientConnection.sendMessage(new Message<>(MessageType.SERVER_STOPPED, null));
                    clientConnection.disconnect();
                }

                for (GameSession session : multiPlayerGameSessions) {
                    session.shutdown();
                }

                for (GameSession session : singlePlayerGameSessions) {
                    session.shutdown();
                }

                executorService.shutdownNow();
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }

                logger.info("Server stopped...");
            } catch (IOException e) {
                logger.severe("Failed to close server: " + e.getMessage());
            } finally {
                cleanup();
            }
        }
    }

    /**
     * Cleans up the resources.
     */
    private void cleanup() {
        waitingClientConnections.clear();
        allAvailableClientConnections.clear();
        multiPlayerGameSessions.clear();
        singlePlayerGameSessions.clear();
    }
}
