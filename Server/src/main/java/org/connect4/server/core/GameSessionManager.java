package org.connect4.server.core;

import org.connect4.server.core.network.ClientConnection;
import org.connect4.server.core.session.GameSession;
import org.connect4.server.core.session.GameSessionType;
import org.connect4.server.core.session.MultiPlayerGameSession;
import org.connect4.server.core.session.SinglePlayerGameSession;
import org.connect4.server.logging.ServerLogger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A class that manage both multi-player game sessions, and single-player game sessions.
 * @author Hassan
 */
public class GameSessionManager {
    private static final ServerLogger LOGGER = ServerLogger.getLogger();

    private final List<GameSession> multiPlayerGameSessions;
    private final List<GameSession> singlePlayerGameSessions;
    private final Map<ClientConnection, GameSession> clientConnectionGameSessionMap;
    private final ExecutorService sessionService;

    /**
     * Constructs a new GameSessionManager.
     */
    public GameSessionManager() {
        this.multiPlayerGameSessions = new CopyOnWriteArrayList<>();
        this.singlePlayerGameSessions = new CopyOnWriteArrayList<>();
        this.clientConnectionGameSessionMap = new ConcurrentHashMap<>();
        this.sessionService = Executors.newCachedThreadPool();
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
     * Removes the specified game session.
     * @param gameSession The game session to be removed.
     */
    public void removeGameSession(GameSession gameSession) {
        if (gameSession != null) {
            if (gameSession.getType() == GameSessionType.MULTI_PLAYER_GAME_SESSION) {
                removeMultiPlayerGameSession((MultiPlayerGameSession) gameSession);
            } else if (gameSession.getType() == GameSessionType.SINGLE_PLAYER_GAME_SESSION) {
                removeSinglePlayerGameSession((SinglePlayerGameSession) gameSession);
            }
        }
    }

    /**
     * Adds a multi-player game session.
     * @param multiPlayerGameSession The multi-player game session to add.
     */
    private void addMultiPlayerGameSession(MultiPlayerGameSession multiPlayerGameSession) {
        if (multiPlayerGameSession != null) {
            multiPlayerGameSessions.add(multiPlayerGameSession);
        }
    }

    /**
     * Removes a multi-player game session.
     * @param multiPlayerGameSession The multi-player game session to remove.
     */
    private void removeMultiPlayerGameSession(MultiPlayerGameSession multiPlayerGameSession) {
        if (multiPlayerGameSession != null) {
            multiPlayerGameSessions.remove(multiPlayerGameSession);
            removeClientsFromSession(multiPlayerGameSession);
        }
    }

    /**
     * Adds a single-player game session.
     * @param singlePlayerGameSession The single-player game session to add.
     */
    private void addSinglePlayerGameSession(SinglePlayerGameSession singlePlayerGameSession) {
        if (singlePlayerGameSession != null) {
            singlePlayerGameSessions.add(singlePlayerGameSession);
        }
    }

    /**
     * Removes a single-player game session.
     * @param singlePlayerGameSession The single-player game session to remove.
     */
    private void removeSinglePlayerGameSession(SinglePlayerGameSession singlePlayerGameSession) {
        if (singlePlayerGameSession != null) {
            singlePlayerGameSessions.remove(singlePlayerGameSession);
            removeClientsFromSession(singlePlayerGameSession);
        }
    }

    /**
     * Associates a client with a game session.
     * @param clientConnection The client connection to associate.
     * @param gameSession The game session to associate the client with.
     */
    public void addClientToGameSession(ClientConnection clientConnection, GameSession gameSession) {
        if (clientConnection != null && gameSession != null) {
            clientConnectionGameSessionMap.put(clientConnection, gameSession);
        }
    }

    /**
     * Gets the game session associated with a client.
     * @param clientConnection The client connection.
     * @return The associated game session, or null if none exists.
     */
    public GameSession getGameSessionForClient(ClientConnection clientConnection) {
        return clientConnectionGameSessionMap.get(clientConnection);
    }

    /**
     * Associates a client with a game session.
     * @param clientConnections The list of client connections to associate.
     * @param gameSession The game session to associate the list of clients with.
     */
    public void addClientsToGameSession(List<ClientConnection> clientConnections, GameSession gameSession) {
        if (clientConnections != null && gameSession != null) {
            clientConnections.forEach(clientConnection -> addClientToGameSession(clientConnection, gameSession));
        }
    }

    /**
     * Removes all clients associated with a game session when the session is removed.
     * @param gameSession The game session.
     */
    private void removeClientsFromSession(GameSession gameSession) {
        clientConnectionGameSessionMap.entrySet()
                .removeIf(entry -> entry.getValue().equals(gameSession));
    }

    /**
     * Starts the specified game session.
     * @param gameSession The game session to be started.
     */
    public void startGameSession(GameSession gameSession) {
        if (gameSession != null) {
            if (gameSession.getType() == GameSessionType.MULTI_PLAYER_GAME_SESSION) {
                startMultiPlayerGameSession((MultiPlayerGameSession) gameSession);
            } else if (gameSession.getType() == GameSessionType.SINGLE_PLAYER_GAME_SESSION) {
                startSinglePlayerGameSession((SinglePlayerGameSession) gameSession);
            }

            sessionService.submit(gameSession);
        }
    }

    /**
     * Starts the specified multi-player game session.
     * @param multiPlayerGameSession The multi-player game session to be started.
     */
    private void startMultiPlayerGameSession(MultiPlayerGameSession multiPlayerGameSession) {
        if (multiPlayerGameSession != null) {
            addMultiPlayerGameSession(multiPlayerGameSession);
            addClientsToGameSession(multiPlayerGameSession.getClients(), multiPlayerGameSession);
        }
    }

    /**
     * Starts the specified single-player game session.
     * @param singlePlayerGameSession The single-player game session to be started.
     */
    private void startSinglePlayerGameSession(SinglePlayerGameSession singlePlayerGameSession) {
        if (singlePlayerGameSession != null) {
            addSinglePlayerGameSession(singlePlayerGameSession);
            addClientsToGameSession(singlePlayerGameSession.getClients(), singlePlayerGameSession);
        }
    }

    /**
     * Restarts the specified game session.
     * @param gameSession The game session to be restarted.
     */
    public void restartGameSession(GameSession gameSession) {
        if (gameSession != null) {
            gameSession.reset();
            addClientsToGameSession(gameSession.getClients(), gameSession);
            sessionService.submit(gameSession);
        }
    }

    /**
     * Shutdown this game session manager.
     */
    public void shutdown() {
        try {
            LOGGER.info("Shutting down GameSessionManager...");

            for (GameSession session : multiPlayerGameSessions) {
                session.shutdown();
            }

            for (GameSession session : singlePlayerGameSessions) {
                session.shutdown();
            }

            sessionService.shutdown();
            if (!sessionService.awaitTermination(5, TimeUnit.SECONDS)) {
                sessionService.shutdownNow();
            }
        } catch (InterruptedException e) {
            LOGGER.severe("Failed to shutdown GameSessionManager: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Cleans up the resources.
     */
    public void cleanup() {
        multiPlayerGameSessions.clear();
        singlePlayerGameSessions.clear();
        clientConnectionGameSessionMap.clear();

        LOGGER.info("Cleaned up game session manager resources.");
    }
}
