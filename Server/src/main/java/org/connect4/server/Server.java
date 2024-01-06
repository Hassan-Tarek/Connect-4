package org.connect4.server;

import javafx.application.Application;
import javafx.stage.Stage;

import org.connect4.server.core.ServerManager;
import org.connect4.server.gui.ServerController;
import org.connect4.server.gui.ServerView;

/**
 * The Main class for the Connect-4 server application.
 * @author Hassan
 */
public class Server extends Application {
    private static final int PORT = 4444;

    /**
     * Start the connect-4 server application.
     * @param stage The primary stage for ths application.
     */
    @Override
    public void start(Stage stage) {
        ServerManager serverManager = new ServerManager(PORT);
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
