package org.connect4.client.gui.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.connect4.client.core.MessageSender;
import org.connect4.client.gui.views.MainView;
import org.connect4.client.utils.Constants;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewController implements BaseController {
    private final Stage stage;
    private final MainView mainView;
    private final MessageSender messageSender;
    private final ExecutorService executorService;

    public MainViewController(Stage stage, MessageSender messageSender) {
        this.stage = stage;
        this.mainView = new MainView();
        this.messageSender = messageSender;
        this.executorService = Executors.newCachedThreadPool();

        initialize();
        setEventHandlers();
    }

    @Override
    public void initialize() {
        Image image = new Image(Objects.requireNonNull(MainView.class.getResource(Constants.CONNECT_4_IMAGE)).toString());
        mainView.getImageView().setImage(image);
        mainView.getPlayOnlineButton().setFocusTraversable(false);
        mainView.getPlayWithComputerButton().setFocusTraversable(false);
    }

    @Override
    public void setEventHandlers() {
        mainView.getPlayOnlineButton().setOnAction(this::playOnline);
        mainView.getPlayWithComputerButton().setOnAction(this::playWithComputer);
        stage.setOnCloseRequest(this::handleOnCloseRequest);
    }

    @Override
    public void showView() {
        Scene scene = new Scene(mainView.getLayout(), Constants.MAIN_VIEW_WIDTH, Constants.MAIN_VIEW_HEIGHT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(Constants.STYLE_SHEET)).toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Connect-4 Game");
        stage.show();
    }

    @Override
    public void closeView() {
        executorService.shutdownNow();
        stage.getScene().setRoot(new Pane());
        stage.close();
    }
    
    public void playOnline(ActionEvent event) {
        executorService.submit(messageSender::requestMultiPlayerGame);
    }

    public void playWithComputer(ActionEvent event) {
        executorService.submit(() -> {
            Platform.runLater(() -> {
                AILevelsViewController aiLevelsViewController = new AILevelsViewController(stage, messageSender::requestSinglePlayerGame);
                aiLevelsViewController.showView();
            });
        });
    }

    private void handleOnCloseRequest(WindowEvent windowEvent) {
        executorService.submit(messageSender::requestDisconnect);
    }
}
