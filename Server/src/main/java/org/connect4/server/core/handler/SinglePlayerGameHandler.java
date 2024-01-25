package org.connect4.server.core.handler;

import org.connect4.game.ai.strategies.AI;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.enums.PlayerType;
import org.connect4.server.core.network.ClientConnection;
import org.connect4.server.core.session.GameSession;

import java.util.Optional;

/**
 * A class that handle the game between a human player and an AI player.
 * @author Hassan.
 */
public class SinglePlayerGameHandler extends GameHandler {
    private final ClientConnection humanPlayerConnection;

    /**
     * Constructs a single-player game handler between a human player and an AI player.
     * @param gameSession The game session.
     * @param humanPlayerConnection The human player connection.
     */
    public SinglePlayerGameHandler(GameSession gameSession, ClientConnection humanPlayerConnection) {
        super(gameSession);
        this.humanPlayerConnection = humanPlayerConnection;
    }

    /**
     * Gets the next move of the current player.
     * @return The next move of the current player.
     */
    @Override
    public Optional<Move> getCurrentPlayerNextMove() {
        if (game.getCurrentPlayer().getPlayerType() == PlayerType.HUMAN) {
            return getNextMove(humanPlayerConnection);
        } else if (game.getCurrentPlayer().getPlayerType() == PlayerType.COMPUTER) {
            return Optional.of(((AI) game.getYellowPlayer()).getNextMove());
        }

        return Optional.empty();
    }
}
