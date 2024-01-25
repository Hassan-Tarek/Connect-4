package org.connect4.server.core.session;

import org.connect4.game.ai.AIFactory;
import org.connect4.game.ai.enums.AIType;
import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.core.Game;
import org.connect4.game.logic.core.Player;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.enums.GameType;
import org.connect4.game.logic.enums.PlayerType;
import org.connect4.server.core.handler.SinglePlayerGameHandler;
import org.connect4.server.core.network.ClientConnection;
import org.connect4.server.core.network.MessageDispatcher;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * A class that manages a single-player game session between a human player and an AI player.
 * @author Hassan
 */
public class SinglePlayerGameSession extends GameSession {
    private final ClientConnection humanPlayerConnection;

    /**
     * Constructs a single-player game session.
     * @param humanPlayerConnection The human player connection.
     * @param aiType The type of AI player.
     * @param messageDispatcher The message dispatcher.
     */
    public SinglePlayerGameSession(ClientConnection humanPlayerConnection, AIType aiType, MessageDispatcher messageDispatcher) {
        super(GameSessionType.SINGLE_PLAYER_GAME_SESSION, messageDispatcher);
        this.humanPlayerConnection = humanPlayerConnection;
        this.countDownLatch = new CountDownLatch(1);

        Board board = new Board();
        Player humanPlayer = new Player(Color.RED, PlayerType.HUMAN);
        Player aiPlayer = AIFactory.getAIPlayer(board, aiType);
        this.game = new Game(board, humanPlayer, aiPlayer, GameType.HUMAN_VS_COMPUTER);
    }

    /**
     * Gets the list of clients.
     * @return The list of clients.
     */
    @Override
    public List<ClientConnection> getClients() {
        return Collections.singletonList(humanPlayerConnection);
    }

    /**
     * Starts the game session between a human player and an AI.
     */
    @Override
    public void run() {
        super.run();

        // Sends the assigned color to the human player
        messageDispatcher.sendAssignedColor(humanPlayerConnection, Color.RED);

        // Start game relay
        gameExecutor.submit(new SinglePlayerGameHandler(this, humanPlayerConnection));
    }
}
