package org.connect4.server.core.handler;

import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.core.Game;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.core.Player;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.enums.GameType;
import org.connect4.game.logic.enums.PlayerType;
import org.connect4.game.logic.exceptions.InvalidMoveException;
import org.connect4.server.core.ClientConnection;
import org.connect4.server.core.session.GameSession;

/**
 * A class that handle a game between two human players.
 * @author Hassan.
 */
public class MultiPlayerGameHandler extends GameHandler {
    private final ClientConnection redPlayerConnection;
    private final ClientConnection yellowPlayerConnection;
    private final Game game;

    /**
     * Constructs a multi-player game handler between two human players.
     * @param gameSession The game session.
     * @param redPlayerConnection The red player connection.
     * @param yellowPlayerConnection The yellow player connection.
     */
    public MultiPlayerGameHandler(GameSession gameSession, ClientConnection redPlayerConnection,
                                  ClientConnection yellowPlayerConnection) {
        super(gameSession);
        this.redPlayerConnection = redPlayerConnection;
        this.yellowPlayerConnection = yellowPlayerConnection;

        // Initializes the game
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
                Move move = null;
                while (move == null) {
                    if (game.getCurrentPlayer() == game.getRedPlayer()) {
                        move = gameSession.getMove(redPlayerConnection);
                    } else if (game.getCurrentPlayer() == game.getYellowPlayer()) {
                        move = gameSession.getMove(yellowPlayerConnection);
                    }
                }

                if (move.isValid(game.getBoard())) {
                    // Sends the move to both players
                    gameSession.sendMoveMessage(redPlayerConnection, move);
                    gameSession.sendMoveMessage(yellowPlayerConnection, move);

                    game.performCurrentPlayerMove(move);

                    // Sends the color of the current player to both players
                    gameSession.sendPlayerTurnMessage(redPlayerConnection, game.getCurrentPlayer().getColor());
                    gameSession.sendPlayerTurnMessage(yellowPlayerConnection, game.getCurrentPlayer().getColor());
                }
            }

            if (game.isOver()) {
                Color winnerColor = null;
                if (game.hasWinner()) {
                    winnerColor = game.getWinner().getColor();
                } else if (game.isDraw()) {
                    winnerColor = Color.NONE;
                }

                // Sends the color of the winner player
                gameSession.sendGameOverMessage(redPlayerConnection, winnerColor);
                gameSession.sendGameOverMessage(yellowPlayerConnection, winnerColor);

                // Sends play again message to both players
                gameSession.sendPlayAgainMessage(redPlayerConnection);
                gameSession.sendPlayAgainMessage(yellowPlayerConnection);
            }
        } catch (InvalidMoveException e) {
            logger.severe("Invalid move: " + e.getMessage());
        }
    }
}
