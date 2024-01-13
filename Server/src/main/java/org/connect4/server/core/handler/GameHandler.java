package org.connect4.server.core.handler;

import org.connect4.server.core.session.GameSession;
import org.connect4.server.logging.ServerLogger;

/**
 * A class that handle the game between the two players.
 * @author Hassan.
 */
public abstract class GameHandler implements Runnable {
    protected static final ServerLogger logger = ServerLogger.getLogger();

    protected final GameSession gameSession;

    /**
     * Constructs a game handler for the specified game.
     * @param gameSession The game session.
     */
    public GameHandler(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    /**
     * Main game loop.
     */
    @Override
    public void run() {
        playGame();
    }

    /**
     * Plays the game.
     */
    public abstract void playGame();
}
