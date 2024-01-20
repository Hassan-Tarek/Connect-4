package org.connect4.server.core;

import org.connect4.game.ai.enums.AIType;
import org.connect4.game.networking.messaging.Message;
import org.connect4.game.networking.messaging.ServerMessageType;
import org.connect4.game.networking.exceptions.SendMessageFailureException;
import org.connect4.server.core.network.ClientConnection;
import org.connect4.server.core.session.GameSession;
import org.connect4.server.core.session.GameSessionType;
import org.connect4.server.core.session.MultiPlayerGameSession;
import org.connect4.server.core.session.SinglePlayerGameSession;
import org.connect4.server.exceptions.ServerStartFailureException;
import org.connect4.server.logging.ServerLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
    private final Map<ClientConnection, GameSession> clientConnectionGameSessionMap;
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
        this.clientConnectionGameSessionMap = new ConcurrentHashMap<>();
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
                    logger.info("client has been accepted with address: " + acceptedClientSocket.getRemoteSocketAddress());

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

            if (!clientConnection.equals(opponentClientConnection)) {
                // Start a new game session
                startMultiPlayerGameSession(clientConnection, opponentClientConnection);
            }
        } else {
            addClientToWaitingList(clientConnection);
        }
    }

    /**
     * Handles the single-player game session by receiving an AI type and starts a game session with that AI.
     * @param clientConnection The client that request single-player game session.
     * @param aiType The selected AI type.
     */
    public void handleSinglePlayerGameSession(ClientConnection clientConnection, AIType aiType) {
        // Start a new game session
        startSinglePlayerGameSession(clientConnection, aiType);
    }

    /**
     * Handles the play-again request from the specified client connection.
     * @param clientConnection The client connection that sent the play-again request.
     */
    public void handleRematchResponse(ClientConnection clientConnection) {
        logger.info("Received rematch response from client: " + clientConnection);

        GameSession gameSession = clientConnectionGameSessionMap.get(clientConnection);
        gameSession.getCountDownLatch().countDown();
        try {
            if (gameSession.getCountDownLatch().await(30, TimeUnit.SECONDS)) {
                if (gameSession.getType() == GameSessionType.MULTI_PLAYER_GAME_SESSION) {
                    startMultiPlayerGameSession(((MultiPlayerGameSession) gameSession).getRedPlayerConnection(),
                            ((MultiPlayerGameSession) gameSession).getYellowPlayerConnection());
                } else if (gameSession.getType() == GameSessionType.SINGLE_PLAYER_GAME_SESSION) {
                    startSinglePlayerGameSession(((SinglePlayerGameSession) gameSession).getHumanPlayerConnection(),
                            ((SinglePlayerGameSession) gameSession).getAiType());
                }
            } else {
                logger.info("Timeout occurred: One or both players did not respond to the rematch request.");
            }
            removeGameSession(gameSession);
        } catch (InterruptedException e) {
            logger.severe("Interrupted while waiting for players to respond: " + e.getMessage());
        }
    }

    /**
     * Handles the leave game session request.
     * @param clientConnection The client connection.
     */
    public void handleLeaveGameSessionRequest(ClientConnection clientConnection) {
        GameSession gameSession = clientConnectionGameSessionMap.get(clientConnection);
        if (gameSession != null) {
            gameSession.shutdown();
            removeGameSession(gameSession);

            if (gameSession.getType() == GameSessionType.MULTI_PLAYER_GAME_SESSION) {
                addClientToWaitingList(((MultiPlayerGameSession) gameSession).getRedPlayerConnection());
                addClientToWaitingList(((MultiPlayerGameSession) gameSession).getYellowPlayerConnection());
            } else if (gameSession.getType() == GameSessionType.SINGLE_PLAYER_GAME_SESSION) {
                addClientToWaitingList(((SinglePlayerGameSession) gameSession).getHumanPlayerConnection());
            }
        }
    }

    /**
     * Handles the client disconnect request.
     * @param clientConnection The client connection.
     */
    public void handleClientDisconnectRequest(ClientConnection clientConnection) {
        GameSession gameSession = clientConnectionGameSessionMap.get(clientConnection);
        if (gameSession != null) {
            gameSession.shutdown();
            removeGameSession(gameSession);
            allAvailableClientConnections.remove(clientConnection);

            if (gameSession.getType() == GameSessionType.MULTI_PLAYER_GAME_SESSION) {
                ClientConnection opponentPlayerConnection = ((MultiPlayerGameSession) gameSession).getOpponentPlayerConnection(clientConnection);
                addClientToWaitingList(opponentPlayerConnection);
            }

            logger.info("Client with address: %s has been disconnected.".formatted(clientConnection.getClientSocket().getRemoteSocketAddress()));
        }

        try {
            clientConnection.sendMessage(new Message<>(ServerMessageType.DISCONNECT_COMPLETED, null));
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send disconnect completed message to the client: " + e.getMessage());
        }
        clientConnection.disconnect();
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
     * shutdown the server.
     */
    public void shutdown() {
        if (running.compareAndSet(true, false)) {
            try {
                for (ClientConnection clientConnection : allAvailableClientConnections) {
                    clientConnection.sendMessage(new Message<>(ServerMessageType.SERVER_STOPPED, null));
                    clientConnection.disconnect();
                }

                for (GameSession session : multiPlayerGameSessions) {
                    session.shutdown();
                }

                for (GameSession session : singlePlayerGameSessions) {
                    session.shutdown();
                }

                if (executorService != null && !executorService.isShutdown()) {
                    executorService.shutdown();

                    if (executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                        executorService.shutdownNow();
                    }
                }

                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }

                logger.info("Server stopped...");
            } catch (IOException | InterruptedException e) {
                logger.severe("Failed to shutdown the server: " + e.getMessage());
            } finally {
                cleanup();
            }
        }
    }

    /**
     * Starts a multi-player game session between the specified client connections.
     * @param clientConnection The first client connection.
     * @param opponentClientConnection The second client connection.
     */
    private void startMultiPlayerGameSession(ClientConnection clientConnection, ClientConnection opponentClientConnection) {
        if (clientConnection != null && clientConnection.isConnected()
                && opponentClientConnection != null && opponentClientConnection.isConnected()
                && !clientConnection.equals(opponentClientConnection)) {
            GameSession gameSession = new MultiPlayerGameSession(clientConnection, opponentClientConnection);
            clientConnectionGameSessionMap.put(clientConnection, gameSession);
            clientConnectionGameSessionMap.put(opponentClientConnection, gameSession);
            addMultiPlayerGameSession(gameSession);

            logger.info("New Multi-Player Game Session has been started.");
        }
    }

    /**
     * Starts a single-player game session between the specified client connection and an AI player of the specified AI type.
     * @param clientConnection The client connection.
     * @param aiType The type of the AI player.
     */
    private void startSinglePlayerGameSession(ClientConnection clientConnection, AIType aiType) {
        if (clientConnection != null && clientConnection.isConnected()
                && aiType != null) {
            GameSession gameSession = new SinglePlayerGameSession(clientConnection, aiType);
            clientConnectionGameSessionMap.put(clientConnection, gameSession);
            addSinglePlayerGameSession(gameSession);

            logger.info("New Single-Player Game Session has been started.");
        }
    }

    /**
     * Adds the given multi-player game session to the list of sessions and submits it to the executor service for execution.
     * @param gameSession The multi-player game session to be added and executed.
     */
    private void addMultiPlayerGameSession(GameSession gameSession) {
        if (gameSession != null) {
            multiPlayerGameSessions.add(gameSession);
            executorService.submit(gameSession);
        }
    }

    /**
     * Adds the given single-player game session to the list of sessions and submits it to the executor service for execution.
     * @param gameSession The single-player game session to be added and executed.
     */
    private void addSinglePlayerGameSession(GameSession gameSession) {
        if (gameSession != null) {
            singlePlayerGameSessions.add(gameSession);
            executorService.submit(gameSession);
        }
    }

    /**
     * Removes the specified game session from game sessions list.
     * @param gameSession The game session to be removed.
     */
    private void removeGameSession(GameSession gameSession) {
        if (gameSession != null) {
            if (gameSession.getType() == GameSessionType.MULTI_PLAYER_GAME_SESSION) {
                multiPlayerGameSessions.remove(gameSession);
                logger.info("Multi-Player game session has been removed from multi-player game sessions list.");
            } else if (gameSession.getType() == GameSessionType.SINGLE_PLAYER_GAME_SESSION) {
                singlePlayerGameSessions.remove(gameSession);
                logger.info("Single-Player game session has been removed from single-player game sessions list.");
            }
        }
    }

    /**
     * Adds the specified client connection to the waiting list.
     * @param clientConnection The client connection to be added to the waiting list.
     */
    private void addClientToWaitingList(ClientConnection clientConnection) {
        if (clientConnection != null && clientConnection.isConnected()) {
            waitingClientConnections.add(clientConnection);
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

        logger.info("Cleaned up server resources.");
    }
}
