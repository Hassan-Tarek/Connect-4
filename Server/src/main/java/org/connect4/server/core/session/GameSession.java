package org.connect4.server.core.session;

import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.networking.Message;
import org.connect4.game.networking.MessageType;
import org.connect4.game.networking.exceptions.ReceiveMessageFailureException;
import org.connect4.game.networking.exceptions.SendMessageFailureException;
import org.connect4.server.core.ServerManager;
import org.connect4.server.logging.ServerLogger;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A class that manages a game session.
 * @author Hassan
 */
public abstract class GameSession implements Runnable {
    protected static final ServerLogger logger = ServerLogger.getLogger();

    protected final ServerManager serverManager;
    protected final ExecutorService gameExecutor;

    /**
     * Construct a game session.
     * @param serverManager The server manager.
     */
    public GameSession(ServerManager serverManager) {
        this.serverManager = serverManager;
        this.gameExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * Starts the game session.
     */
    @Override
    public void run() {
        startGameSession();
    };

    /**
     * Starts the game session between two players.
     */
    public abstract void startGameSession();

    /**
     * Sends the start game message to the specified player's socket.
     * @param playerSocket The player's socket to which the message will be sent.
     */
    public void sendStartGameMessage(Socket playerSocket) {
        try {
            Message<Void> startGameMessage = new Message<>(MessageType.START_GAME, null);
            serverManager.sendMessage(playerSocket, startGameMessage);
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send start game messages: " + e.getMessage());
        }
    }

    /**
     * Sends the color message to the specified player's socket.
     * @param playerSocket The player's socket to which the color message will be sent.
     * @param color The color to be sent.
     */
    public void sendColorMessage(Socket playerSocket, Color color) {
        try {
            Message<Color> colorMessage = new Message<>(MessageType.COLOR, color);
            serverManager.sendMessage(playerSocket, colorMessage);
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send color message: " + e.getMessage());
        }
    }

    /**
     * Sends the game-over message to the specified player's socket.
     * @param playerSocket The player's socket to which the winnerColor will be sent.
     * @param winnerColor The color of the winner.
     */
    public void sendGameOverMessage(Socket playerSocket, Color winnerColor) {
        try {
            Message<Color> gameOverMessage = new Message<>(MessageType.GAME_OVER, winnerColor);
            serverManager.sendMessage(playerSocket, gameOverMessage);
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send game over message: " + e.getMessage());
        }
    }

    /**
     * Sends the move message to the specified player's socket.
     * @param playerSocket The player's socket to which the move will be sent.
     * @param move The move to be sent.
     */
    public void sendMoveMessage(Socket playerSocket, Move move) {
        try {
            Message<Move> moveMessage = new Message<>(MessageType.MOVE, move);
            serverManager.sendMessage(playerSocket, moveMessage);
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send move message: " + e.getMessage());
        }
    }

    /**
     * Gets the move from the specified player's socket.
     * @param playerSocket The player's socket from which the move will be received.
     * @return The received move.
     */
    public Move getMove(Socket playerSocket) {
        try {
            Message<Move> moveMessage = (Message<Move>) serverManager.receiveMessage(playerSocket);
            return moveMessage.getPayload();
        } catch (ReceiveMessageFailureException e) {
            logger.severe("Failed to receive move message from player: " + e.getMessage());
        }
        return null;
    }

    /**
     * Shuts down the game session.
     */
    public void shutdown() {
        try {
            if (!gameExecutor.isShutdown()) {
                gameExecutor.shutdownNow();
            }
        } catch (Exception e) {
            logger.severe("Error during shutdown: " + e.getMessage());
        }
    }
}
