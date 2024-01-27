package org.connect4.client.logger;

import org.connect4.client.utils.Constants;
import org.connect4.game.logging.BaseLogger;

/**
 * A class that represents the logger for the connect-4 client-side.
 * @author Hassan
 */
public class ClientLogger extends BaseLogger {
    private static ClientLogger logger;

    /**
     * Constructs a new instance of the ClientLogger class.
     */
    private ClientLogger() {
        super(ClientLogger.class.getName(), Constants.LOG_FILE);
    }

    /**
     * Gets the singleton instance of the ClientLogger.
     * @return The ClientLogger.
     */
    public static ClientLogger getLogger() {
        if (logger == null)
            logger = new ClientLogger();
        return logger;
    }
}
