package org.connect4.server;

import javafx.application.Application;
import javafx.stage.Stage;

import org.connect4.server.core.ServerManager;
import org.connect4.server.exceptions.ServerStartFailureException;
import org.connect4.server.gui.ServerController;
import org.connect4.server.gui.ServerView;

public class Server extends Application {
    private static final int PORT = 4444;

    @SuppressWarnings("ClassEscapesDefinedScope")
    @Override
    public void start(Stage stage) throws ServerStartFailureException {
        ServerManager serverManager = new ServerManager(PORT);
        ServerView serverView = ServerView.getInstance(stage);
        ServerController serverController = new ServerController(serverManager, serverView);
        serverView.setController(serverController);
        serverController.initializeEventHandlers();
        serverController.showView();
    }

    public static void main(String[] args) {
        launch();
    }
}
