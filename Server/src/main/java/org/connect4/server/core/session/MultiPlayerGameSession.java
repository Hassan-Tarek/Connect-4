package org.connect4.server.core.session;

import org.connect4.game.logic.enums.Color;
import org.connect4.server.core.ClientConnection;
import org.connect4.server.core.MessageRelay;
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
        super();
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
     * Starts the game session by setting up the message relays between two players.
     */
    @Override
    public void startGameSession() {
        try {
            // Sends start game message to both players
            sendStartGameMessage(redPlayerConnection);
            sendStartGameMessage(yellowPlayerConnection);

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
            // Calls the shutdown method of the super class
            super.shutdown();

            // Shutdown the relay executor
            if (relayExecutor != null && !relayExecutor.isShutdown()) {
                relayExecutor.shutdown();

                if (!relayExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    relayExecutor.shutdownNow();
                }
            }
        } catch (Exception e) {
            logger.severe("Failed to shutdown the multi-player game session: " + e.getMessage());
        }
    }
}
