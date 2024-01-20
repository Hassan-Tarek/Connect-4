package org.connect4.server.core.session;

import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.networking.messaging.Message;
import org.connect4.game.networking.messaging.ServerMessageType;
import org.connect4.game.networking.exceptions.SendMessageFailureException;
import org.connect4.server.core.network.ClientConnection;
import org.connect4.server.logging.ServerLogger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A class that manages a game session.
 * @author Hassan
 */
public abstract class GameSession implements Runnable {
    protected static final ServerLogger logger = ServerLogger.getLogger();

    protected final GameSessionType type;
    protected final ExecutorService gameExecutor;

    protected CountDownLatch countDownLatch;

    /**
     * Constructs a game session.
     */
    public GameSession(GameSessionType type) {
        this.type = type;
        this.gameExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * Gets the type of this game session.
     * @return The type of this game session.
     */
    public GameSessionType getType() {
        return type;
    }

    /**
     * Gets the countdown latch.
     * @return The countdown latch.
     */
    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    /**
     * Starts the game session.
     */
    @Override
    public void run() {
        startGameSession();
    }

    /**
     * Starts the game session between two players.
     */
    public abstract void startGameSession();

    /**
     * Sends a game started message to the specified player's socket.
     * @param playerConnection The player connection to which the message will be sent.
     */
    public void sendGameStartedMessage(ClientConnection playerConnection) {
        try {
            Message<Void> gameStartedMessage = new Message<>(ServerMessageType.GAME_STARTED, null);
            playerConnection.sendMessage(gameStartedMessage);
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send game started message: " + e.getMessage());
        }
    }

    /**
     * Sends a game session ended message to the specified  player's socket.
     * @param playerConnection The player connection to which the message will be sent.
     */
    public void sendGameSessionEndedMessage(ClientConnection playerConnection) {
        try {
            Message<Void> gameStoppedMessage = new Message<>(ServerMessageType.GAME_SESSION_ENDED, null);
            playerConnection.sendMessage(gameStoppedMessage);
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send game stopped message: " + e.getMessage());
        }
    }

    /**
     * Sends the color message to the specified player's socket.
     * @param playerConnection The player connection to which the color message will be sent.
     * @param color The color to be sent.
     */
    public void sendColorMessage(ClientConnection playerConnection, Color color) {
        try {
            Message<Color> colorMessage = new Message<>(ServerMessageType.COLOR, color);
            playerConnection.sendMessage(colorMessage);
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send color message: " + e.getMessage());
        }
    }

    /**
     * Sends the game-over message to the specified player's socket.
     * @param playerConnection The player connection to which the game-over message will be sent.
     * @param winnerColor The color of the winner.
     */
    public void sendGameOverMessage(ClientConnection playerConnection, Color winnerColor) {
        try {
            Message<Color> gameOverMessage = new Message<>(ServerMessageType.GAME_OVER, winnerColor);
            playerConnection.sendMessage(gameOverMessage);
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send game over message: " + e.getMessage());
        }
    }

    /**
     * Sends a rematch request message to the specified player's socket.
     * @param playerConnection The player connection to which the rematch request message will be sent.
     */
    public void sendRematchRequestMessage(ClientConnection playerConnection) {
        try {
            Message<Void> playAgainMessage = new Message<>(ServerMessageType.REMATCH_REQUEST, null);
            playerConnection.sendMessage(playAgainMessage);
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to play again message: " + e.getMessage());
        }
    }

    /**
     * Sends the move message to the specified player's socket.
     * @param playerConnection The player connection to which the move will be sent.
     * @param move The move to be sent.
     */
    public void sendMoveMessage(ClientConnection playerConnection, Move move) {
        try {
            Message<Move> moveMessage = new Message<>(ServerMessageType.MOVE, move);
            playerConnection.sendMessage(moveMessage);
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send move message: " + e.getMessage());
        }
    }

    /**
     * Sends player-turn message to the specified player's socket.
     * @param playerConnection The player connection to which the player-turn message will be sent.
     * @param currentPlayerColor The color of the current player.
     */
    public void sendPlayerTurnMessage(ClientConnection playerConnection, Color currentPlayerColor) {
        try {
            Message<Color> playerTurnMessage = new Message<>(ServerMessageType.PLAYER_TURN, currentPlayerColor);
            playerConnection.sendMessage(playerTurnMessage);
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send player turn message: " + e.getMessage());
        }
    }

    /**
     * Gets the move from the specified player's socket.
     * @param playerConnection The player connection from which the move will be received.
     * @return The received move.
     */
    public Move getMove(ClientConnection playerConnection) {
        try {
            if (!playerConnection.getMoveMessageQueue().isEmpty()) {
                Message<Move> moveMessage = playerConnection.getMoveMessageQueue().take();
                return moveMessage.getPayload();
            }
        } catch (InterruptedException e) {
            logger.severe("Failed to receive move message from player: " + e.getMessage());
        }
        return null;
    }

    /**
     * Shuts down the game session.
     */
    public void shutdown() {
        try {
            if (gameExecutor != null && !gameExecutor.isShutdown()) {
                gameExecutor.shutdown();

                if (!gameExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                    gameExecutor.shutdownNow();
                }
            }
        } catch (Exception e) {
            logger.severe("Failed to shutdown the game session: " + e.getMessage());
        }
    }
}
