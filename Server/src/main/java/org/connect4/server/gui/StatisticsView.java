package org.connect4.server.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StatisticsView {
    private static final int VIEW_WIDTH = 400;
    private static final int VIEW_HEIGHT = 300;

    private final Stage stage;
    private final Scene scene;
    private final VBox layout;
    private final ObservableList<PieChart.Data> pieChartData;
    private final PieChart pieChart;

    private StatisticsController controller;

    private static StatisticsView view;

    private StatisticsView() {
        this.stage = new Stage();
        this.layout = new VBox();
        this.scene = new Scene(layout, VIEW_WIDTH, VIEW_HEIGHT);
        this.pieChartData = FXCollections.observableArrayList();
        this.pieChart = new PieChart(pieChartData);

        initialize();
    }

    public static StatisticsView getInstance() {
        if (view == null)
            view = new StatisticsView();
        return view;
    }

    public ObservableList<PieChart.Data> getPieChartData() {
        return pieChartData;
    }

    public void setController(StatisticsController controller) {
        this.controller = controller;
    }

    public void show() {
        stage.show();
    }

    private void initialize() {
        pieChart.setData(pieChartData);
        pieChart.setTitle("Server Statistics");

        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(pieChart);

        stage.setTitle("Server Statistics");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
    }
}
