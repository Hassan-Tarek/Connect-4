package org.connect4.logging;

/**
 * A logger class designed for the ai.
 * @author Hassan
 */
public class AILogger extends BaseLogger {
    private static final String LOG_FILE = "ai.log";

    private static AILogger logger;

    /**
     * Constructs a new instance of the AILogger class.
     */
    private AILogger() {
        super(AILogger.class.getName(), LOG_FILE);
    }

    /**
     * Gets the singleton instance of the AILogger.
     * @return The AILogger.
     */
    public static AILogger getLogger() {
        if (logger == null) {
            logger = new AILogger();
        }
        return logger;
    }
}
