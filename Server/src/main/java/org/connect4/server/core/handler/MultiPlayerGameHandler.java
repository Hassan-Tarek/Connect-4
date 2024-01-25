package org.connect4.server.core.handler;

import org.connect4.game.logic.core.Move;
import org.connect4.server.core.network.ClientConnection;
import org.connect4.server.core.session.GameSession;

import java.util.Optional;

/**
 * A class that handle a game between two human players.
 * @author Hassan.
 */
public class MultiPlayerGameHandler extends GameHandler {
    private final ClientConnection redPlayerConnection;
    private final ClientConnection yellowPlayerConnection;

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
    }

    /**
     * Gets the next move of the current player.
     * @return The next move of the current player.
     */
    @Override
    public Optional<Move> getCurrentPlayerNextMove() {
        ClientConnection currentPlayerConnection = getCurrentPlayerConnection();
        return getNextMove(currentPlayerConnection);
    }

    /**
     * Gets the current player connection.
     * @return The connection of the current player.
     */
    private ClientConnection getCurrentPlayerConnection() {
        return game.getCurrentPlayer() == game.getRedPlayer() ? redPlayerConnection : yellowPlayerConnection;
    }
}
