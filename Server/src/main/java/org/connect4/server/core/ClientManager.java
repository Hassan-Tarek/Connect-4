package org.connect4.server.core;

import org.connect4.server.core.network.ClientConnection;
import org.connect4.server.logging.ServerLogger;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * A class that manages connected and waiting clients for the game server.
 * @author Hassan
 */
public class ClientManager {
    private static final ServerLogger LOGGER = ServerLogger.getLogger();

    private final Set<ClientConnection> connectedClients;
    private final Set<ClientConnection> waitingClients;

    /**
     * Constructs a new ClientManager.
     */
    public ClientManager() {
        this.connectedClients = new ConcurrentSkipListSet<>();
        this.waitingClients = new ConcurrentSkipListSet<>();
    }

    /**
     * Gets a list of connected client connections.
     * @return The list of connected client connections.
     */
    public Set<ClientConnection> getConnectedClients() {
        return connectedClients;
    }

    /**
     * Gets a list of waiting client connections.
     * @return The list of waiting client connections.
     */
    public Set<ClientConnection> getWaitingClients() {
        return waitingClients;
    }

    /**
     * Adds newly connected client connection to the list of connected clients.
     * @param clientConnection The newly connected client connection.
     */
    public void addConnectedClient(ClientConnection clientConnection) {
        if (clientConnection != null && clientConnection.isConnected()) {
            connectedClients.add(clientConnection);
        }
    }

    /**
     * Removes the specified client connection from the connected clients list.
     * @param clientConnection The client connection to be removed.
     */
    public void removeConnectedClient(ClientConnection clientConnection) {
        if (clientConnection != null) {
            connectedClients.remove(clientConnection);
        }
    }

    /**
     * Adds the specified client connection to the waiting list.
     * @param clientConnection The client connection to be added to the waiting list.
     */
    public void addClientToWaitingList(ClientConnection clientConnection) {
        if (clientConnection != null && clientConnection.isConnected()) {
            waitingClients.add(clientConnection);
        }
    }

    /**
     * Adds the specified client connection to the waiting list.
     * @param clientConnections The client connection to be added.
     */
    public void addClientsToWaitingList(List<ClientConnection> clientConnections) {
        for (ClientConnection clientConnection : clientConnections) {
            addClientToWaitingList(clientConnection);
        }
    }

    /**
     * Removes the specified client connection from the waiting list.
     * @param clientConnection The client connection to be removed.
     */
    public void removeClientFromWaitingList(ClientConnection clientConnection) {
        if (clientConnection != null) {
            waitingClients.remove(clientConnection);
        }
    }

    /**
     * Finds a match client for the specified client connection.
     * @param clientConnection The client connection.
     * @return The matched client connection.
     */
    public Optional<ClientConnection> findMatchedClient(ClientConnection clientConnection) {
        if (!waitingClients.isEmpty()) {
            for (ClientConnection nextClientConnection : waitingClients) {
                if (!clientConnection.equals(nextClientConnection)) {
                    return Optional.of(nextClientConnection);
                } else {
                    System.out.println("Equals: " + clientConnection + " : " + nextClientConnection);
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Disconnects All connected client connections.
     */
    public void disconnectClients() {
        for (ClientConnection clientConnection : connectedClients) {
            clientConnection.disconnect();

            LOGGER.info("Disconnected client: " + clientConnection);
        }

    }

    /**
     * Cleans up the resources.
     */
    public void cleanup() {
        connectedClients.clear();
        waitingClients.clear();

        LOGGER.info("Cleaned up client manager resources.");
    }
}
