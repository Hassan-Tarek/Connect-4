package org.connect4.server.core;

import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.core.Game;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.core.Player;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.enums.GameType;
import org.connect4.game.logic.enums.PlayerType;
import org.connect4.game.networking.Message;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A class that manages a game session between two clients.
 * @author Hassan
 */
public class GameSession implements Runnable {
    private final ServerManager serverManager;
    private final Socket firstClientSocket;
    private final Socket secondClientSocket;
    private final Game game;
    private final ExecutorService relayExecutor;
    private final ExecutorService gameExecutor;

    /**
     * Construct a game session between two clients.
     * @param serverManager The server manager.
     * @param firstClientSocket The first client socket.
     * @param secondClientSocket The second client socket.
     */
    public GameSession(ServerManager serverManager, Socket firstClientSocket, Socket secondClientSocket) {
        this.serverManager = serverManager;
        this.firstClientSocket = firstClientSocket;
        this.secondClientSocket = secondClientSocket;
        this.game = new Game(new Board(),
                new Player(Color.RED, PlayerType.HUMAN),
                new Player(Color.YELLOW, PlayerType.HUMAN),
                GameType.HUMAN_VS_HUMAN);
        this.relayExecutor = Executors.newFixedThreadPool(2);
        this.gameExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * Starts the game session by setting up the message relays between two clients.
     */
    @Override
    public void run() {
        try {
            BlockingQueue<Message<Move>> moveMessageQueue = new LinkedBlockingQueue<>();

            // Start message relays
            relayExecutor.submit(new MessageRelay(serverManager, moveMessageQueue, firstClientSocket, secondClientSocket));
            relayExecutor.submit(new MessageRelay(serverManager, moveMessageQueue, secondClientSocket, firstClientSocket));

            // Start game relay
            gameExecutor.submit(new GameHandler(serverManager, game, moveMessageQueue, firstClientSocket, secondClientSocket));
        } catch (Exception e) {
            System.err.println("ERROR: Failed to start message relay: " + e.getMessage());
        } finally {
            relayExecutor.shutdown();
        }
    }

    /**
     * Shuts down the game session.
     */
    public void shutdown() {
        relayExecutor.shutdownNow();
        serverManager.closeSocket(firstClientSocket);
        serverManager.closeSocket(secondClientSocket);
    }
}
