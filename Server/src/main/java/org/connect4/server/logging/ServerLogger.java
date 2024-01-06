package org.connect4.server.logging;

import org.connect4.game.logging.BaseLogger;

/**
 * A class represents the logger for connect-4 server-side.
 * @author Hassan
 */
public class ServerLogger extends BaseLogger {
    private static final String LOG_FILE = "server.log";

    private static ServerLogger logger;

    /**
     * Constructs a new instance of the ServerLogger class.
     */
    private ServerLogger() {
        super(ServerLogger.class.getName(), LOG_FILE);
    }

    /**
     * Gets the singleton instance of the ServerLogger.
     * @return The ServerLogger.
     */
    public static ServerLogger getLogger() {
        if (logger == null) {
            logger = new ServerLogger();
        }
        return logger;
    }
}
