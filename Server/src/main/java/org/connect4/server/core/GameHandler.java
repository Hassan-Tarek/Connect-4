package org.connect4.server.core;

import org.connect4.server.logging.ServerLogger;
import org.connect4.game.logic.core.Game;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.exceptions.InvalidMoveException;
import org.connect4.game.networking.Message;
import org.connect4.game.networking.MessageType;
import org.connect4.game.networking.exceptions.SendMessageFailureException;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * A class that handle the game between the two players.
 * @author Hassan.
 */
public class GameHandler implements Runnable {
    private static final ServerLogger logger = ServerLogger.getLogger();

    private final ServerManager serverManager;
    private final Game game;
    private final BlockingQueue<Message<Move>> moveMessageQueue;
    private final Socket redPlayerSocket;
    private final Socket yellowPlayerSocket;

    /**
     * Constructs a game handler for the specified game.
     * @param serverManager The server manager.
     * @param game The game to manage.
     * @param moveMessageQueue The move message queue.
     * @param redPlayerSocket The red player socket.
     * @param yellowPlayerSocket The yellow player socket.
     */
    public GameHandler(ServerManager serverManager, Game game, BlockingQueue<Message<Move>> moveMessageQueue,
                       Socket redPlayerSocket, Socket yellowPlayerSocket) {
        this.serverManager = serverManager;
        this.game = game;
        this.moveMessageQueue = moveMessageQueue;
        this.redPlayerSocket = redPlayerSocket;
        this.yellowPlayerSocket = yellowPlayerSocket;
    }

    /**
     * Main game loop.
     */
    @Override
    public void run() {
        playGame();
    }

    /**
     * Plays the game.
     */
    private void playGame() {
        try {
            sendStartGameMessage();

            sendPlayerColors();

            while (!game.isGameOver()) {
                Message<Move> moveMessage = moveMessageQueue.take();
                Move move = moveMessage.getPayload();

                if (move.isValid(game.getBoard())) {
                    // Sends the move to both players
                    serverManager.sendMessage(redPlayerSocket, moveMessage);
                    serverManager.sendMessage(yellowPlayerSocket, moveMessage);

                    game.performCurrentPlayerMove(move);
                }
            }

            if (game.hasWinner()) {
                sendGameOverMessage(game.getWinner().getColor());
            } else if (game.isDraw()) {
                sendGameOverMessage(Color.NONE);
            }

        } catch (InterruptedException e) {
            logger.severe("Failed to retrieve a move message from move message queue: " + e.getMessage());
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send message: " + e.getMessage());
        } catch (InvalidMoveException e) {
            logger.severe("Invalid move: " + e.getMessage());
        }
    }

    /**
     * Sends the start game message to both players.
     */
    private void sendStartGameMessage() {
        try {
            Message<Void> startGameMessage = new Message<>(MessageType.START_GAME, null);
            serverManager.sendMessage(redPlayerSocket, startGameMessage);
            serverManager.sendMessage(yellowPlayerSocket, startGameMessage);
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send start game messages: " + e.getMessage());
        }
    }

    /**
     * Sends the color of each player.
     */
    private void sendPlayerColors() {
        try {
            serverManager.sendMessage(redPlayerSocket, new Message<>(MessageType.COLOR, Color.RED));
            serverManager.sendMessage(yellowPlayerSocket, new Message<>(MessageType.COLOR, Color.YELLOW));
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send player colors: " + e.getMessage());
        }
    }

    /**
     * Sends the game-over message to both players.
     * @param winnerColor The color of the winner.
     */
    private void sendGameOverMessage(Color winnerColor) {
        try {
            Message<Color> gameOverMessage = new Message<>(MessageType.GAME_OVER, winnerColor);
            serverManager.sendMessage(redPlayerSocket, gameOverMessage);
            serverManager.sendMessage(yellowPlayerSocket, gameOverMessage);
        } catch (SendMessageFailureException e) {
            logger.severe("Failed to send game over message: " + e.getMessage());
        }
    }
}
