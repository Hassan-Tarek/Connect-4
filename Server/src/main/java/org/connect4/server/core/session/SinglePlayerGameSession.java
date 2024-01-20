package org.connect4.server.core.session;

import org.connect4.game.ai.enums.AIType;
import org.connect4.game.logic.enums.Color;
import org.connect4.server.core.network.ClientConnection;
import org.connect4.server.core.handler.SinglePlayerGameHandler;

import java.util.concurrent.CountDownLatch;

/**
 * A class that manages a single-player game session between a human player and an AI player.
 * @author Hassan
 */
public class SinglePlayerGameSession extends GameSession {
    private final ClientConnection humanPlayerConnection;
    private final AIType aiType;

    /**
     * Constructs a single-player game session.
     * @param humanPlayerConnection The human player connection.
     * @param aiType The type of AI player.
     */
    public SinglePlayerGameSession(ClientConnection humanPlayerConnection, AIType aiType) {
        super(GameSessionType.SINGLE_PLAYER_GAME_SESSION);
        this.humanPlayerConnection = humanPlayerConnection;
        this.aiType = aiType;
        this.countDownLatch = new CountDownLatch(1);
    }

    /**
     * Gets the human player connection.
     * @return The human player connection.
     */
    public ClientConnection getHumanPlayerConnection() {
        return humanPlayerConnection;
    }

    /**
     * Gets the AI type.
     * @return The AI type.
     */
    public AIType getAiType() {
        return aiType;
    }

    /**
     * Starts the game session between a human player and an AI.
     */
    @Override
    public void startGameSession() {
        try {
            // Sends the game started message to the human player
            sendGameStartedMessage(humanPlayerConnection);

            // Sends the color to the human player
            sendColorMessage(humanPlayerConnection, Color.RED);

            // Start game relay
            gameExecutor.submit(new SinglePlayerGameHandler(this, humanPlayerConnection, aiType));
        } finally {
            shutdown();
        }
    }

    /**
     * Shuts down the game session.
     */
    @Override
    public void shutdown() {
        // Sends game session ended message to the human player connection
        sendGameSessionEndedMessage(humanPlayerConnection);

        super.shutdown();
    }
}
