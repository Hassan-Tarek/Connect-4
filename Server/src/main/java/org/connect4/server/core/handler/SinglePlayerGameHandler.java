package org.connect4.server.core.handler;

import org.connect4.game.ai.AIFactory;
import org.connect4.game.ai.enums.AIType;
import org.connect4.game.ai.strategies.AI;
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
 * A class that handle the game between a human player and an AI player.
 * @author Hassan.
 */
public class SinglePlayerGameHandler extends GameHandler {
    private final ClientConnection humanPlayerConnection;
    private final Game game;

    /**
     * Constructs a single-player game handler between a human player and an AI player.
     * @param gameSession The game session.
     * @param humanPlayerConnection The human player connection.
     * @param aiType The AI type.
     */
    public SinglePlayerGameHandler(GameSession gameSession, ClientConnection humanPlayerConnection, AIType aiType) {
        super(gameSession);
        this.humanPlayerConnection = humanPlayerConnection;

        // Initializes the game
        Board board = new Board();
        Player humanPlayer = new Player(Color.RED, PlayerType.HUMAN);
        Player aiPlayer = AIFactory.getAIPlayer(board, aiType);
        this.game = new Game(board, humanPlayer, aiPlayer, GameType.HUMAN_VS_COMPUTER);
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
                    if (game.getCurrentPlayer().getPlayerType() == PlayerType.HUMAN) {
                        move = gameSession.getMove(humanPlayerConnection);
                    } else if (game.getCurrentPlayer().getPlayerType() == PlayerType.COMPUTER) {
                        move = ((AI) game.getYellowPlayer()).getNextMove();
                    }
                }

                if (move.isValid(game.getBoard())) {
                    // Sends the move to human player
                    gameSession.sendMoveMessage(humanPlayerConnection, move);

                    game.performCurrentPlayerMove(move);

                    // Sends the color of the current player
                    gameSession.sendPlayerTurnMessage(humanPlayerConnection, game.getCurrentPlayer().getColor());
                }

                if (game.isOver()) {
                    Color winnerColor = null;
                    if (game.hasWinner()) {
                        winnerColor = game.getWinner().getColor();
                    } else if (game.isDraw()) {
                        winnerColor = Color.NONE;
                    }

                    // Sends the color of the winner to the human player connection
                    gameSession.sendGameOverMessage(humanPlayerConnection, winnerColor);

                    // Sends play again message to the human player connection
                    gameSession.sendPlayAgainMessage(humanPlayerConnection);
                }
            }
        } catch (InvalidMoveException e) {
            logger.severe("Invalid move: " + e.getMessage());
        }
    }
}
