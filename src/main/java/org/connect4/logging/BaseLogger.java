package org.connect4.logging;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A base class for all provided loggers.
 * @author Hassan
 */
public class BaseLogger extends Logger {
    /**
     * Constructs a new BaseLogger instance with the given name and log file name.
     * @param name The name of the logger.
     * @param filename The name of the log file to which the log message will be written.
     */
    protected BaseLogger(String name, String filename) {
        super(name, null);
        this.setLevel(Level.INFO);
        setupHandlers(filename);
    }

    /**
     * Initializes the file and console handlers for logging.
     * @param filename The name of the log file to which the log message will be written.
     */
    private void setupHandlers(String filename) {
        try {
            FileHandler fileHandler = new FileHandler(filename);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.INFO);
            this.addHandler(fileHandler);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize file logging system: " + e);
        }

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        consoleHandler.setLevel(Level.INFO);
        this.addHandler(consoleHandler);
    }
}
