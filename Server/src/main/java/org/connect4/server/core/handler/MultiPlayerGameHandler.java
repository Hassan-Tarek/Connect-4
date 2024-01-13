package org.connect4.server.core.handler;

import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.core.Game;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.core.Player;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.enums.GameType;
import org.connect4.game.logic.enums.PlayerType;
import org.connect4.game.logic.exceptions.InvalidMoveException;
import org.connect4.game.networking.Message;
import org.connect4.server.core.session.GameSession;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * A class that handle a game between two human players.
 * @author Hassan.
 */
public class MultiPlayerGameHandler extends GameHandler {
    private final Socket redPlayerSocket;
    private final Socket yellowPlayerSocket;
    private final BlockingQueue<Message<Move>> moveMessageQueue;
    private final Game game;

    /**
     * Constructs a multi-player game handler between two human players.
     * @param gameSession The game session.
     * @param redPlayerSocket The red player socket.
     * @param yellowPlayerSocket The yellow player socket.
     * @param moveMessageQueue The move message queue.
     */
    public MultiPlayerGameHandler(GameSession gameSession, Socket redPlayerSocket, Socket yellowPlayerSocket,
                                  BlockingQueue<Message<Move>> moveMessageQueue) {
        super(gameSession);
        this.redPlayerSocket = redPlayerSocket;
        this.yellowPlayerSocket = yellowPlayerSocket;
        this.moveMessageQueue = moveMessageQueue;
        this.game = new Game(new Board(),
                new Player(Color.RED, PlayerType.HUMAN),
                new Player(Color.YELLOW, PlayerType.HUMAN),
                GameType.HUMAN_VS_HUMAN);
    }

    /**
     * Plays the game.
     */
    @Override
    public void playGame() {
        try {
            while (!game.isOver()) {
                Message<Move> moveMessage = moveMessageQueue.take();
                Move move = moveMessage.getPayload();

                if (move.isValid(game.getBoard())) {
                    // Sends the move to both players
                    gameSession.sendMoveMessage(redPlayerSocket, move);
                    gameSession.sendMoveMessage(yellowPlayerSocket, move);

                    game.performCurrentPlayerMove(move);
                }
            }

            if (game.hasWinner() || game.isDraw()) {
                Color winnerColor = null;
                if (game.hasWinner()) {
                    winnerColor = game.getWinner().getColor();
                } else if (game.isDraw()) {
                    winnerColor = Color.NONE;
                }

                gameSession.sendGameOverMessage(redPlayerSocket, winnerColor);
                gameSession.sendGameOverMessage(yellowPlayerSocket, winnerColor);
            }
        } catch (InterruptedException e) {
            logger.severe("Failed to retrieve a move message from move message queue: " + e.getMessage());
        } catch (InvalidMoveException e) {
            logger.severe("Invalid move: " + e.getMessage());
        }
    }
}
