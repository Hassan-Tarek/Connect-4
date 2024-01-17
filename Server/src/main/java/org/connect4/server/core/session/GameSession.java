package org.connect4.server.core.session;

import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.networking.Message;
import org.connect4.game.networking.MessageType;
import org.connect4.game.networking.exceptions.ReceiveMessageFailureException;
import org.connect4.game.networking.exceptions.SendMessageFailureException;
import org.connect4.server.core.ClientConnection;
import org.connect4.server.logging.ServerLogger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class that manages a game session.
 * @author Hassan
 */
public abstract class GameSession implements Runnable {
    protected static final ServerLogger logger = ServerLogger.getLogger();

    protected final ExecutorService gameExecutor;

    /**
     * Constructs a game session.
     */
    public GameSession() {
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
     * @param playerConnection The player connection to which the message will be sent.
     */
    public void sendStartGameMessage(ClientConnection playerConnection) {
        try {
            Message<Void> startGameMessage = new Message<>(MessageType.START_GAME, null);
            playerConnection.sendMessage(startGameMessage);
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send start game messages: " + e.getMessage());
        }
    }

    /**
     * Sends the color message to the specified player's socket.
     * @param playerConnection The player connection to which the color message will be sent.
     * @param color The color to be sent.
     */
    public void sendColorMessage(ClientConnection playerConnection, Color color) {
        try {
            Message<Color> colorMessage = new Message<>(MessageType.COLOR, color);
            playerConnection.sendMessage(colorMessage);
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send color message: " + e.getMessage());
        }
    }

    /**
     * Sends the game-over message to the specified player's socket.
     * @param playerConnection The player's socket to which the winnerColor will be sent.
     * @param winnerColor The color of the winner.
     */
    public void sendGameOverMessage(ClientConnection playerConnection, Color winnerColor) {
        try {
            Message<Color> gameOverMessage = new Message<>(MessageType.GAME_OVER, winnerColor);
            playerConnection.sendMessage(gameOverMessage);
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send game over message: " + e.getMessage());
        }
    }

    /**
     * Sends the move message to the specified player's socket.
     * @param playerConnection The player connection to which the move will be sent.
     * @param move The move to be sent.
     */
    public void sendMoveMessage(ClientConnection playerConnection, Move move) {
        try {
            Message<Move> moveMessage = new Message<>(MessageType.MOVE, move);
            playerConnection.sendMessage(moveMessage);
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send move message: " + e.getMessage());
        }
    }

    /**
     * Gets the move from the specified player's socket.
     * @param playerConnection The player connection from which the move will be received.
     * @return The received move.
     */
    @SuppressWarnings("unchecked")
    public Move getMove(ClientConnection playerConnection) {
        try {
            Message<Move> moveMessage = (Message<Move>) playerConnection.receiveMessage();
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
