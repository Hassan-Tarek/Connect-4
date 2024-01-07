package org.connect4.server.gui.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.stage.WindowEvent;

import org.connect4.server.core.ServerManager;
import org.connect4.server.gui.views.ServerView;
import org.connect4.server.gui.views.StatisticsView;

/**
 * A controller class for the server view.
 * @author Hassan
 */
public class ServerController {
    private final ServerManager serverManager;
    private final ServerView serverView;

    /**
     * Constructs a new ServerController with the specified server manager and server view.
     * @param serverManager The server manager.
     * @param serverView The server view.
     */
    public ServerController(ServerManager serverManager, ServerView serverView) {
        this.serverManager = serverManager;
        this.serverView = serverView;
    }

    /**
     * Initializes the event handlers for the server view.
     */
    public void initializeEventHandlers() {
        serverView.setEventHandlers();
    }

    /**
     * Shows the server view.
     */
    public void showView() {
        serverView.show();
    }

    /**
     * Starts the server.
     * @param event The action event
     */
    public void startServer(ActionEvent event) {
        serverManager.start();
        serverView.getStartButton().setDisable(true);
        serverView.getStopButton().setDisable(false);
        serverView.getShowStatisticsButton().setDisable(false);
    }

    /**
     * Stops the server.
     * @param event The action event.
     */
    public void stopServer(ActionEvent event) {
        serverManager.shutdown();
        serverView.getStartButton().setDisable(false);
        serverView.getStopButton().setDisable(true);
        serverView.getShowStatisticsButton().setDisable(true);
    }

    /**
     * Shows the statistics view.
     * @param event The action event.
     */
    public void showStatisticsWindow(ActionEvent event) {
        StatisticsView statisticsView = StatisticsView.getInstance();
        StatisticsController statisticsController = new StatisticsController(serverManager, statisticsView);
        statisticsView.setController(statisticsController);

        statisticsController.showView();
        statisticsController.control();
    }

    /**
     * Closes the server window.
     * @param event The action event.
     */
    public void closeView(WindowEvent event) {
        stopServer(null);
        Platform.exit();
        System.exit(0);
    }
}
