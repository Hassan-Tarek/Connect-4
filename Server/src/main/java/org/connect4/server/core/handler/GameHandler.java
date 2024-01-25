package org.connect4.server.core.handler;

import org.connect4.game.logic.core.Game;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.core.Player;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.exceptions.InvalidMoveException;
import org.connect4.server.core.network.ClientConnection;
import org.connect4.server.core.network.MessageDispatcher;
import org.connect4.server.core.session.GameSession;
import org.connect4.server.logging.ServerLogger;

import java.util.Optional;

/**
 * A class that handle the game between the two players.
 * @author Hassan.
 */
public abstract class GameHandler implements Runnable {
    protected static final ServerLogger LOGGER = ServerLogger.getLogger();

    protected final GameSession gameSession;
    protected final MessageDispatcher messageDispatcher;
    protected final Game game;

    /**
     * Constructs a game handler for the specified game.
     * @param gameSession The game session.
     */
    public GameHandler(GameSession gameSession) {
        this.gameSession = gameSession;
        this.messageDispatcher = gameSession.getMessageDispatcher();
        this.game = gameSession.getGame();
    }

    /**
     * Plays the game.
     */
    @Override
    public void run() {
        while (!game.isOver()) {
            Optional<Move> currentPlayerNextMove = getCurrentPlayerNextMove();
            currentPlayerNextMove.ifPresent(this::processMove);
        }

        handleGameOver();
    }

    /**
     * Gets the next move of the current player.
     * @return The next move of the current player.
     */
    protected abstract Optional<Move> getCurrentPlayerNextMove();

    /**
     * Gets the next move of the specified client connection.
     * @param clientConnection The client connection.
     * @return The next move.
     */
    protected Optional<Move> getNextMove(ClientConnection clientConnection) {
        try {
            if (!clientConnection.getMoveMessageQueue().isEmpty()) {
                return Optional.of(clientConnection.getMoveMessageQueue().take().getPayload());
            }
        } catch (InterruptedException e) {
            LOGGER.severe("Failed to get the next move of the client: " + clientConnection);
        }

        return Optional.empty();
    }

    /**
     * Processes the specified move.
     * @param move The move to process.
     */
    private void processMove(Move move) {
        if (move.isValid(game.getBoard())) {
            executeMove(move);
        } else {
            LOGGER.warning("Invalid move: " + move);
        }
    }

    /**
     * Executes the specified valid move.
     * @param move The valid move to execute.
     */
    private void executeMove(Move move) {
        try {
            messageDispatcher.broadcastPlayerMove(gameSession.getClients(), move);
            game.performCurrentPlayerMove(move);
            messageDispatcher.broadcastPlayerTurn(gameSession.getClients(), game.getCurrentPlayer().getColor());
        } catch (InvalidMoveException e) {
            LOGGER.severe("Invalid move: " + e.getMessage());
        }
    }

    /**
     * Handles game over.
     */
    private void handleGameOver() {
        gameSession.setRunning(false);

        Optional<Player> winner = game.getWinner();
        Color winnerColor = winner.map(Player::getColor).orElse(Color.NONE);
        winner.ifPresent(Player::incrementScore);
        // broadcast winner color
        messageDispatcher.broadcastGameOver(gameSession.getClients(), winnerColor);
    }
}
