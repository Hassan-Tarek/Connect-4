package org.connect4.server.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class ServerView {
    private static final int VIEW_WIDTH = 350;
    private static final int VIEW_HEIGHT = 300;
    private static final double LAYOUT_SPACING = 20.0;

    private final Stage stage;
    private final Scene scene;
    private final VBox layout;
    private final Label label;
    private final Button startButton;
    private final Button stopButton;
    private final Button showStatisticsButton;

    private ServerController controller;

    private static ServerView view;

    private ServerView(Stage stage) {
        this.stage = stage;
        this.layout = new VBox(LAYOUT_SPACING);
        this.scene = new Scene(layout, VIEW_WIDTH, VIEW_HEIGHT);
        this.label = new Label("Connect-4 Server");
        this.startButton = new Button("Start Server");
        this.stopButton = new Button("Stop Server");
        this.showStatisticsButton = new Button("Show Statistics");

        initialize();
    }

    public static ServerView getInstance(Stage stage) {
        if (view == null) {
            view = new ServerView(stage);
        }
        return view;
    }

    public Button getStartButton() {
        return startButton;
    }

    public Button getStopButton() {
        return stopButton;
    }

    public Button getShowStatisticsButton() {
        return showStatisticsButton;
    }

    public void setController(ServerController controller) {
        this.controller = controller;
    }

    public void setEventHandlers() {
        if (controller != null) {
            startButton.setOnAction(controller::startServer);
            stopButton.setOnAction(controller::stopServer);
            showStatisticsButton.setOnAction(controller::showStatisticsWindow);
            stage.setOnCloseRequest(controller::closeView);
        }
    }

    public void show() {
        stage.show();
    }

    private void initialize() {
        // Buttons setup
        startButton.setId("startServerButton");

        stopButton.setId("stopServerButton");
        stopButton.setDisable(true);

        showStatisticsButton.setId("showStatisticsButton");
        showStatisticsButton.setDisable(true);

        // Layout setup
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(label, startButton, stopButton, showStatisticsButton);

        // Scene setup
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

        // Stage setup
        stage.setTitle("Connect-4 Server");
        stage.setScene(scene);
    }
}
