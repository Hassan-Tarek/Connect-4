package org.connect4.server;

import javafx.application.Application;
import javafx.stage.Stage;

import org.connect4.server.core.ServerManager;
import org.connect4.server.gui.controllers.ServerController;
import org.connect4.server.gui.views.ServerView;
import org.connect4.server.utils.ConfigLoader;

/**
 * The Main class for the Connect-4 server application.
 * @author Hassan
 */
public class Server extends Application {
    /**
     * Start the connect-4 server application.
     * @param stage The primary stage for ths application.
     */
    @Override
    public void start(Stage stage) {
        // Loads the server configurations
        ConfigLoader serverConfig = new ConfigLoader();
        serverConfig.loadProperties();
        int port = serverConfig.getServerPort();

        ServerManager serverManager = new ServerManager(port);
        ServerView serverView = ServerView.getInstance(stage);
        ServerController serverController = new ServerController(serverManager, serverView);
        serverView.setController(serverController);
        serverController.initializeEventHandlers();
        serverController.showView();
    }

    /**
     * The main entry point for connect-4 server application.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch();
    }
}
