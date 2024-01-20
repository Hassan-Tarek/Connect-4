package org.connect4.server.core.session;

import org.connect4.game.logic.enums.Color;
import org.connect4.server.core.network.ClientConnection;
import org.connect4.server.core.network.MessageRelay;
import org.connect4.server.core.handler.MultiPlayerGameHandler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A class that manages a multi-player game session between two human players.
 * @author Hassan
 */
public class MultiPlayerGameSession extends GameSession {
    private final ClientConnection redPlayerConnection;
    private final ClientConnection yellowPlayerConnection;
    private final ExecutorService relayExecutor;

    /**
     * Constructs a multi-player game session.
     * @param redPlayerConnection The red player connection.
     * @param yellowPlayerConnection The yellow player connection.
     */
    public MultiPlayerGameSession(ClientConnection redPlayerConnection, ClientConnection yellowPlayerConnection) {
        super(GameSessionType.MULTI_PLAYER_GAME_SESSION);
        this.redPlayerConnection = redPlayerConnection;
        this.yellowPlayerConnection = yellowPlayerConnection;
        this.relayExecutor = Executors.newFixedThreadPool(2);
        this.countDownLatch = new CountDownLatch(2);
    }

    /**
     * Gets the red player connection.
     * @return The red player connection.
     */
    public ClientConnection getRedPlayerConnection() {
        return redPlayerConnection;
    }

    /**
     * Gets the yellow player connection.
     * @return The yellow player connection.
     */
    public ClientConnection getYellowPlayerConnection() {
        return yellowPlayerConnection;
    }

    /**
     * Gets the opponent player connection of the specified player connection.
     * @param playerConnection The player connection.
     * @return The opponent player connection.
     */
    public ClientConnection getOpponentPlayerConnection(ClientConnection playerConnection) {
        if (playerConnection.equals(redPlayerConnection)) {
            return yellowPlayerConnection;
        } else if (playerConnection.equals(yellowPlayerConnection)) {
            return redPlayerConnection;
        }

        return null;
    }

    /**
     * Starts the game session by setting up the message relays between two players.
     */
    @Override
    public void startGameSession() {
        try {
            // Sends game started message to both players
            sendGameStartedMessage(redPlayerConnection);
            sendGameStartedMessage(yellowPlayerConnection);

            // Sends color to both players
            sendColorMessage(redPlayerConnection, Color.RED);
            sendColorMessage(yellowPlayerConnection, Color.YELLOW);

            // Start text message relays
            relayExecutor.submit(new MessageRelay(redPlayerConnection, yellowPlayerConnection));
            relayExecutor.submit(new MessageRelay(yellowPlayerConnection, redPlayerConnection));

            // Start game
            gameExecutor.submit(new MultiPlayerGameHandler(this, redPlayerConnection, yellowPlayerConnection));
        } finally {
            shutdown();
        }
    }

    /**
     * Shuts down the game session.
     */
    @Override
    public void shutdown() {
        try {
            // Sends game session ended message to both players
            sendGameSessionEndedMessage(redPlayerConnection);
            sendGameSessionEndedMessage(yellowPlayerConnection);

            // Calls the shutdown method of the super class
            super.shutdown();

            // Shutdown the relay executor
            if (relayExecutor != null && !relayExecutor.isShutdown()) {
                relayExecutor.shutdown();

                if (!relayExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                    relayExecutor.shutdownNow();
                }
            }
        } catch (Exception e) {
            logger.severe("Failed to shutdown the multi-player game session: " + e.getMessage());
        }
    }
}
