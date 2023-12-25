package org.connect4.server.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.stage.WindowEvent;

import org.connect4.server.core.ServerManager;

public class ServerController {
    private final ServerManager serverManager;
    private final ServerView serverView;

    public ServerController(ServerManager serverManager, ServerView serverView) {
        this.serverManager = serverManager;
        this.serverView = serverView;
    }

    public void initializeEventHandlers() {
        serverView.setEventHandlers();
    }

    public void showView() {
        serverView.show();
    }

    public void startServer(ActionEvent event) {
        serverManager.start();
        serverView.getStartButton().setDisable(true);
        serverView.getStopButton().setDisable(false);
        serverView.getShowStatisticsButton().setDisable(false);
    }

    public void stopServer(ActionEvent event) {
        serverManager.shutdown();
        serverView.getStartButton().setDisable(false);
        serverView.getStopButton().setDisable(true);
        serverView.getShowStatisticsButton().setDisable(true);
    }

    public void showStatisticsWindow(ActionEvent event) {

    }

    public void closeView(WindowEvent event) {
        stopServer(null);
        Platform.exit();
        System.exit(0);
    }
}
