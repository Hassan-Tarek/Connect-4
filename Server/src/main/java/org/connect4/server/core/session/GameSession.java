package org.connect4.server.core.session;

import org.connect4.game.logic.core.Game;
import org.connect4.server.core.network.ClientConnection;
import org.connect4.server.core.network.MessageDispatcher;
import org.connect4.server.logging.ServerLogger;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class that manages a game session.
 * @author Hassan
 */
public abstract class GameSession implements Runnable {
    protected static final ServerLogger LOGGER = ServerLogger.getLogger();

    protected final GameSessionType type;
    protected final MessageDispatcher messageDispatcher;
    protected final AtomicBoolean isRunning;
    protected ExecutorService gameExecutor;
    protected CountDownLatch countDownLatch;

    protected Game game;

    /**
     * Constructs a game session.
     */
    public GameSession(GameSessionType type, MessageDispatcher messageDispatcher) {
        this.type = type;
        this.messageDispatcher = messageDispatcher;
        this.gameExecutor = Executors.newSingleThreadExecutor();
        this.isRunning = new AtomicBoolean(false);
    }

    /**
     * Gets the type of this game session.
     * @return The type of this game session.
     */
    public GameSessionType getType() {
        return type;
    }

    /**
     * Gets the message dispatcher.
     * @return The message dispatcher.
     */
    public MessageDispatcher getMessageDispatcher() {
        return messageDispatcher;
    }

    /**
     * Checks if this game session is running.
     * @return true if it is running, false otherwise.
     */
    public boolean isRunning() {
        return isRunning.get();
    }

    /**
     * Sets the running state of this game session.
     * @param isRunning true to set the game session as running, false otherwise.
     */
    public void setRunning(boolean isRunning) {
        this.isRunning.set(isRunning);
    }

    /**
     * Gets the countdown latch.
     * @return The countdown latch.
     */
    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    /**
     * Gets the game.
     * @return The game.
     */
    public Game getGame() {
        return game;
    }

    /**
     * Gets the list of clients.
     * @return The list of clients.
     */
    public abstract List<ClientConnection> getClients();

    /**
     * Starts the game session.
     */
    @Override
    public void run() {
        this.setRunning(true);

        // Sends the game started message to both players
        messageDispatcher.broadcastGameStarted(this.getClients());

        // Sends the player scores
        messageDispatcher.broadcastPlayerScores(this.getClients(), game.getRedPlayer().getScore(), game.getYellowPlayer().getScore());
    }

    /**
     * Resets this game session.
     */
    public void reset() {
        this.isRunning.set(false);
        this.gameExecutor = Executors.newCachedThreadPool();
        this.game.reset();
    }

    /**
     * Shuts down the game session.
     */
    public void shutdown() {
        try {
            this.setRunning(false);

            if (gameExecutor != null && !gameExecutor.isShutdown()) {
                gameExecutor.shutdown();

                if (!gameExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    gameExecutor.shutdownNow();
                }
            }

            messageDispatcher.broadcastGameSessionEnded(this.getClients());
        } catch (Exception e) {
            LOGGER.severe("Failed to shutdown the game session: " + e.getMessage());
        }
    }
}
