package org.connect4.logging;

public class GameLogger extends BaseLogger {
    private static final String LOG_FILE = "game.log";

    private static GameLogger logger;

    private GameLogger() {
        super(GameLogger.class.getName(), LOG_FILE);
    }

    public static GameLogger getLogger() {
        if (logger == null) {
            logger = new GameLogger();
        }
        return logger;
    }
}
