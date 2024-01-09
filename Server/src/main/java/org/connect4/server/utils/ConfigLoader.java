package org.connect4.server.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A class to load configuration properties.
 * @author Hassan
 */
public class ConfigLoader {
    private final Properties properties;

    /**
     * Constructs a ConfigLoader instance.
     */
    public ConfigLoader() {
        this.properties = new Properties();
    }

    /**
     * Loads the properties from the configuration file.
     */
    public void loadProperties() {
        try (InputStream inputStream = getClass().getResourceAsStream(Constants.CONFIG_FILE)) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the server port number.
     * @return The server port.
     */
    public int getServerPort() {
        return Integer.parseInt(properties.getProperty("server.port"));
    }
}
