package org.connect4.server.core.session;

import org.connect4.game.ai.enums.AIType;
import org.connect4.game.logic.enums.Color;
import org.connect4.server.core.ClientConnection;
import org.connect4.server.core.handler.SinglePlayerGameHandler;

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
        super();
        this.humanPlayerConnection = humanPlayerConnection;
        this.aiType = aiType;
    }

    /**
     * Starts the game session between a human player and an AI.
     */
    @Override
    public void startGameSession() {
        // Sends the start game message to the human player
        sendStartGameMessage(humanPlayerConnection);

        // Sends the color to the human player
        sendColorMessage(humanPlayerConnection, Color.RED);

        // Start game relay
        gameExecutor.submit(new SinglePlayerGameHandler(this, humanPlayerConnection, aiType));
    }

    /**
     * Shuts down the game session.
     */
    @Override
    public void shutdown() {
        super.shutdown();
        humanPlayerConnection.disconnect();
    }
}
