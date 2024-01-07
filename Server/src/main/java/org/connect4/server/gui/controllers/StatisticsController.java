package org.connect4.server.gui.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.PieChart;
import javafx.util.Duration;

import org.connect4.server.core.ServerManager;
import org.connect4.server.gui.views.StatisticsView;

/**
 * A controller class for the statistics view.
 * @author Hassan
 */
public class StatisticsController {
    private final ServerManager serverManager;
    private final StatisticsView statisticsView;
    private final Timeline timeline;

    /**
     * Constructs a new StatisticsController with the specified server manager and statistics view.
     * @param serverManager The server manager.
     * @param statisticsView The statistics view.
     */
    public StatisticsController(ServerManager serverManager, StatisticsView statisticsView) {
        this.serverManager = serverManager;
        this.statisticsView = statisticsView;
        this.timeline = new Timeline();
    }

    /**
     * Shows the statistics view.
     */
    public void showView() {
        statisticsView.show();
    }

    /**
     * Controls the statistics view by updating it every second.
     */
    public void control() {
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), updateStatisticsView());
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Updates the statistics view.
     * @return An event handler that update the statistics view.
     */
    private EventHandler<ActionEvent> updateStatisticsView() {
        return event -> {
            int currentGameSessionsSize = serverManager.getGameSessions().size();
            int currentWaitingSocketsSize = serverManager.getWaitingSockets().size();

            if (statisticsView.getPieChartData().isEmpty()) {
                statisticsView.getPieChartData().add(new PieChart.Data("Game Sessions", currentGameSessionsSize));
                statisticsView.getPieChartData().add(new PieChart.Data("Waiting Clients", currentWaitingSocketsSize));
            } else {
                PieChart.Data gameSessionsData = statisticsView.getPieChartData().get(0);
                PieChart.Data waitingClientsData = statisticsView.getPieChartData().get(1);

                if (gameSessionsData.getPieValue() != currentGameSessionsSize) {
                    gameSessionsData.setPieValue(currentGameSessionsSize);
                }

                if (waitingClientsData.getPieValue() != currentWaitingSocketsSize) {
                    waitingClientsData.setPieValue(currentWaitingSocketsSize);
                }
            }
        };
    }
}