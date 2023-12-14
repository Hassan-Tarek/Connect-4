package org.connect4.logging;

/**
 * A logger class designed for the Game logic.
 * @author Hassan
 */
public class GameLogger extends BaseLogger {
    private static final String LOG_FILE = "game.log";

    private static GameLogger logger;

    /**
     * Constructs a new instance of the GameLogger class.
     */
    private GameLogger() {
        super(GameLogger.class.getName(), LOG_FILE);
    }

    /**
     * Gets the singleton instance of the GameLogger.
     * @return The GameLogger.
     */
    public static GameLogger getLogger() {
        if (logger == null) {
            logger = new GameLogger();
        }
        return logger;
    }
}
