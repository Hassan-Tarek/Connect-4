package org.connect4.server.core.session;

import org.connect4.game.ai.AIFactory;
import org.connect4.game.ai.enums.AIType;
import org.connect4.game.logic.core.Board;
import org.connect4.game.logic.core.Game;
import org.connect4.game.logic.core.Player;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.logic.enums.GameType;
import org.connect4.game.logic.enums.PlayerType;
import org.connect4.server.core.ServerManager;
import org.connect4.server.core.handler.MultiPlayerGameHandler;
import org.connect4.server.core.handler.SinglePlayerGameHandler;

import java.net.Socket;

/**
 * A class that manages a single-player game session between a human player and an AI playr.
 * @author Hassan
 */
public class SinglePlayerGameSession extends GameSession {
    private final Socket humanPlayerSocket;
    private final AIType aiType;

    public SinglePlayerGameSession(ServerManager serverManager, Socket humanPlayerSocket, AIType aiType) {
        super(serverManager);
        this.humanPlayerSocket = humanPlayerSocket;
        this.aiType = aiType;
    }

    /**
     * Starts the game session between a human player and an AI.
     */
    @Override
    public void startGameSession() {
        // Sends the start game message to the human player
        sendStartGameMessage(humanPlayerSocket);

        // Sends the color to the human player
        sendColorMessage(humanPlayerSocket, Color.RED);

        // Start game relay
        gameExecutor.submit(new SinglePlayerGameHandler(this, humanPlayerSocket, aiType));
    }

    /**
     * Shuts down the game session.
     */
    @Override
    public void shutdown() {
        super.shutdown();
        serverManager.closeSocket(humanPlayerSocket);
    }
}
