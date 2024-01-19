package org.connect4.server.core.session;

import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.networking.Message;
import org.connect4.game.networking.MessageType;
import org.connect4.game.networking.exceptions.SendMessageFailureException;
import org.connect4.server.core.ClientConnection;
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

    protected final ExecutorService gameExecutor;

    protected CountDownLatch countDownLatch;

    /**
     * Constructs a game session.
     */
    public GameSession() {
        this.gameExecutor = Executors.newSingleThreadExecutor();
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
     * Sends the start game message to the specified player's socket.
     * @param playerConnection The player connection to which the message will be sent.
     */
    public void sendStartGameMessage(ClientConnection playerConnection) {
        try {
            Message<Void> startGameMessage = new Message<>(MessageType.START_GAME, null);
            playerConnection.sendMessage(startGameMessage);
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send start game message: " + e.getMessage());
        }
    }

    /**
     * Sends the stop game message to the specified  player's socket.
     * @param playerConnection The player connection to which the message will be sent.
     */
    public void sendStopGameMessage(ClientConnection playerConnection) {
        try {
            Message<Void> stopGameMessage = new Message<>(MessageType.GAME_STOPPED, null);
            playerConnection.sendMessage(stopGameMessage);
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send stop game message: " + e.getMessage());
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
     * @param playerConnection The player connection to which the game-over message will be sent.
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
     * Sends the play-again message to the specified player's socket.
     * @param playerConnection The player connection to which the play-again message will be sent.
     */
    public void sendPlayAgainMessage(ClientConnection playerConnection) {
        try {
            Message<Void> playAgainMessage = new Message<>(MessageType.PLAY_AGAIN, null);
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
            Message<Move> moveMessage = new Message<>(MessageType.MOVE, move);
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
            Message<Color> playerTurnMessage = new Message<>(MessageType.PLAYER_TURN, currentPlayerColor);
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

                if (!gameExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    gameExecutor.shutdownNow();
                }
            }
        } catch (Exception e) {
            logger.severe("Failed to shutdown the game session: " + e.getMessage());
        }
    }
}
