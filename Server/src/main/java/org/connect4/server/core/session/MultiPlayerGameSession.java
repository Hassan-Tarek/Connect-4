package org.connect4.server.core.session;

import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.networking.Message;
import org.connect4.server.core.ClientConnection;
import org.connect4.server.core.MessageRelay;
import org.connect4.server.core.handler.MultiPlayerGameHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A class that manages a multi-player game session between two human players.
 * @author Hassan
 */
public class MultiPlayerGameSession extends GameSession {
    private final ClientConnection redPlayerConnection;
    private final ClientConnection yellowPlayerConnection;
    private final ExecutorService relayExecutor;
    private final BlockingQueue<Message<Move>> moveMessageQueue;

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
        this.moveMessageQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Starts the game session by setting up the message relays between two players.
     */
    @Override
    public void startGameSession() {
        // Sends start game message to both players
        sendStartGameMessage(redPlayerConnection);
        sendStartGameMessage(yellowPlayerConnection);

        // Sends color to both players
        sendColorMessage(redPlayerConnection, Color.RED);
        sendColorMessage(yellowPlayerConnection, Color.YELLOW);

        // Start message relays
        relayExecutor.submit(new MessageRelay(redPlayerConnection, yellowPlayerConnection, moveMessageQueue));
        relayExecutor.submit(new MessageRelay(yellowPlayerConnection, redPlayerConnection, moveMessageQueue));

        // Start game relay
        gameExecutor.submit(new MultiPlayerGameHandler(this, redPlayerConnection, yellowPlayerConnection, moveMessageQueue));
    }

    /**
     * Shuts down the game session.
     */
    @Override
    public void shutdown() {
        try {
            super.shutdown();
            if (!relayExecutor.isShutdown()) {
                relayExecutor.shutdownNow();
            }
            redPlayerConnection.disconnect();
            yellowPlayerConnection.disconnect();
        } catch (Exception e) {
            logger.severe("Error during shutdown: " + e.getMessage());
        }
    }
}
