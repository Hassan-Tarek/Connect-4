package org.connect4.logging;

public class AILogger extends BaseLogger {
    private static final String LOG_FILE = "ai.log";

    private static AILogger logger;

    private AILogger() {
        super(AILogger.class.getName(), LOG_FILE);
    }

    public static AILogger getLogger() {
        if (logger == null) {
            logger = new AILogger();
        }
        return logger;
    }
}
