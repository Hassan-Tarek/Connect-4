package org.connect4.server.core.session;

import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.core.Game;
import org.connect4.game.logic.core.Player;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.enums.GameType;
import org.connect4.game.logic.enums.PlayerType;
import org.connect4.server.core.network.ClientConnection;
import org.connect4.server.core.network.MessageDispatcher;
import org.connect4.server.core.network.MessageRelay;
import org.connect4.server.core.handler.MultiPlayerGameHandler;

import java.util.List;
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

    private ExecutorService relayExecutor;

    /**
     * Constructs a multi-player game session.
     * @param redPlayerConnection The red player connection.
     * @param yellowPlayerConnection The yellow player connection.
     * @param messageDispatcher The message dispatcher.
     */
    public MultiPlayerGameSession(ClientConnection redPlayerConnection, ClientConnection yellowPlayerConnection, MessageDispatcher messageDispatcher) {
        super(GameSessionType.MULTI_PLAYER_GAME_SESSION, messageDispatcher);
        this.redPlayerConnection = redPlayerConnection;
        this.yellowPlayerConnection = yellowPlayerConnection;
        this.relayExecutor = Executors.newFixedThreadPool(2);
        this.countDownLatch = new CountDownLatch(2);
        this.game = new Game(new Board(),
                new Player(Color.RED, PlayerType.HUMAN),
                new Player(Color.YELLOW, PlayerType.HUMAN),
                GameType.HUMAN_VS_HUMAN);
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
     * Gets the list of clients.
     * @return The list of clients.
     */
    @Override
    public List<ClientConnection> getClients() {
        return List.of(redPlayerConnection, yellowPlayerConnection);
    }

    /**
     * Starts the game session by setting up the message relays between two players.
     */
    @Override
    public void run() {
        super.run();

        // Sends the assigned color to each player
        messageDispatcher.sendAssignedColor(redPlayerConnection, Color.RED);
        messageDispatcher.sendAssignedColor(yellowPlayerConnection, Color.YELLOW);

        // Start text message relays
        relayExecutor.submit(new MessageRelay(this, redPlayerConnection, yellowPlayerConnection));
        relayExecutor.submit(new MessageRelay(this, yellowPlayerConnection, redPlayerConnection));

        // Start game
        gameExecutor.submit(new MultiPlayerGameHandler(this, redPlayerConnection, yellowPlayerConnection));
    }

    /**
     * Resets this game session.
     */
    @Override
    public void reset() {
        super.reset();

        this.countDownLatch = new CountDownLatch(2);
        this.relayExecutor = Executors.newFixedThreadPool(2);
    }

    /**
     * Shuts down the game session.
     */
    @Override
    public void shutdown() {
        try {
            super.shutdown();

            // Shutdown the relay executor
            if (relayExecutor != null && !relayExecutor.isShutdown()) {
                relayExecutor.shutdown();

                if (!relayExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    relayExecutor.shutdownNow();
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Failed to shutdown the multi-player game session: " + e.getMessage());
        }
    }
}
