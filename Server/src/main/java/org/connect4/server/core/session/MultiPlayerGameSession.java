package org.connect4.server.core.session;

import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.core.Game;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.core.Player;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.enums.GameType;
import org.connect4.game.logic.enums.PlayerType;
import org.connect4.game.networking.Message;
import org.connect4.server.core.MessageRelay;
import org.connect4.server.core.ServerManager;
import org.connect4.server.core.handler.MultiPlayerGameHandler;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class MultiPlayerGameSession extends GameSession {
    private final Socket redPlayerSocket;
    private final Socket yellowPlayerSocket;
    private final ExecutorService relayExecutor;
    private final BlockingQueue<Message<Move>> moveMessageQueue;

    public MultiPlayerGameSession(ServerManager serverManager, Socket redPlayerSocket, Socket yellowPlayerSocket) {
        super(serverManager);
        this.redPlayerSocket = redPlayerSocket;
        this.yellowPlayerSocket = yellowPlayerSocket;
        this.relayExecutor = Executors.newFixedThreadPool(2);
        this.moveMessageQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Starts the game session by setting up the message relays between two players.
     */
    @Override
    public void startGameSession() {
        try {
            // Sends start game message to both players
            sendStartGameMessage(redPlayerSocket);
            sendStartGameMessage(yellowPlayerSocket);

            // Sends color to both players
            sendColorMessage(redPlayerSocket, Color.RED);
            sendColorMessage(yellowPlayerSocket, Color.YELLOW);

            // Start message relays
            relayExecutor.submit(new MessageRelay(serverManager, moveMessageQueue, redPlayerSocket, yellowPlayerSocket));
            relayExecutor.submit(new MessageRelay(serverManager, moveMessageQueue, yellowPlayerSocket, redPlayerSocket));

            // Start game relay
            gameExecutor.submit(new MultiPlayerGameHandler(this, redPlayerSocket, yellowPlayerSocket, moveMessageQueue));
        } catch (Exception e) {
            logger.severe("Failed to start message relay: " + e.getMessage());
        } finally {
            relayExecutor.shutdown();
            gameExecutor.shutdown();
        }
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
            serverManager.closeSocket(redPlayerSocket);
            serverManager.closeSocket(yellowPlayerSocket);
        } catch (Exception e) {
            logger.severe("Error during shutdown: " + e.getMessage());
        }
    }
}
