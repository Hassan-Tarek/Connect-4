package org.connect4.server.gui.views;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.connect4.server.gui.controllers.ServerController;
import org.connect4.server.utils.Constants;

import java.util.Objects;

/**
 * A view class for the server.
 * @author Hassan
 */
public class ServerView {
    private final Stage stage;
    private final Scene scene;
    private final VBox layout;
    private final Label label;
    private final Button startButton;
    private final Button stopButton;
    private final Button showStatisticsButton;

    private ServerController controller;

    private static ServerView view;

    /**
     * Constructs a new ServerView with the specified stage.
     * @param stage The stage.
     */
    private ServerView(Stage stage) {
        this.stage = stage;
        this.layout = new VBox(Constants.LAYOUT_SPACING);
        this.scene = new Scene(layout, Constants.SERVER_VIEW_WIDTH, Constants.SERVER_VIEW_HEIGHT);
        this.label = new Label("Connect-4 Server");
        this.startButton = new Button("Start Server");
        this.stopButton = new Button("Stop Server");
        this.showStatisticsButton = new Button("Show Statistics");

        initialize();
    }

    /**
     * Gets a singleton instance of the ServerView.
     * @param stage The stage.
     * @return The ServerView instance.
     */
    public static ServerView getInstance(Stage stage) {
        if (view == null) {
            view = new ServerView(stage);
        }
        return view;
    }

    /**
     * Gets the start button.
     * @return The start button.
     */
    public Button getStartButton() {
        return startButton;
    }

    /**
     * Gets the stop button.
     * @return The stop button.
     */
    public Button getStopButton() {
        return stopButton;
    }

    /**
     * Gets the show statistics button.
     * @return The show statistics button.
     */
    public Button getShowStatisticsButton() {
        return showStatisticsButton;
    }

    /**
     * Sets the controller for this view.
     * @param controller The controller to be set.
     */
    public void setController(ServerController controller) {
        this.controller = controller;
    }

    /**
     * Sets the event handlers for this view buttons.
     */
    public void setEventHandlers() {
        if (controller != null) {
            startButton.setOnAction(controller::startServer);
            stopButton.setOnAction(controller::stopServer);
            showStatisticsButton.setOnAction(controller::showStatisticsWindow);
            stage.setOnCloseRequest(controller::closeView);
        }
    }

    /**
     * Shows the server view.
     */
    public void show() {
        stage.show();
    }

    /**
     * Initializes the server view components and layout.
     */
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
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(Constants.STYLE_SHEET)).toExternalForm());

        // Stage setup
        stage.setTitle("Connect-4 Server");
        stage.setScene(scene);
    }
}
