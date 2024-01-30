package org.connect4.client.gui.views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import org.connect4.client.utils.Constants;

public class MainView {
    private final BorderPane layout;
    private final ImageView imageView;
    private final Button playOnlineButton;
    private final Button playWithComputerButton;

    public MainView() {
        this.layout = new BorderPane();
        this.imageView = new ImageView();
        this.playOnlineButton = new Button("Play Online");
        this.playWithComputerButton = new Button("Play With Computer");

        createLayout();
    }

    public BorderPane getLayout() {
        return layout;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Button getPlayOnlineButton() {
        return playOnlineButton;
    }

    public Button getPlayWithComputerButton() {
        return playWithComputerButton;
    }

    private void createLayout() {
        // ImageView setup
        imageView.setFitWidth(2 * Constants.MAIN_VIEW_WIDTH / 3);
        imageView.setFitHeight(Constants.MAIN_VIEW_HEIGHT);

        // Button setup
        playOnlineButton.setMinWidth(150);
        playOnlineButton.setMinHeight(40);
        playOnlineButton.setId("playOnlineButton");

        playWithComputerButton.setMinWidth(150);
        playWithComputerButton.setMinHeight(40);
        playWithComputerButton.setId("playWithComputerButton");

        // Align rightLayout and add children
        VBox buttonsLayout = new VBox(Constants.LAYOUT_PADDING);
        buttonsLayout.setAlignment(Pos.CENTER);
        buttonsLayout.setMinWidth(Constants.MAIN_VIEW_WIDTH / 3);
        buttonsLayout.setMinHeight(Constants.MAIN_VIEW_HEIGHT);
        buttonsLayout.getChildren().addAll(playOnlineButton, playWithComputerButton);

        layout.setLeft(imageView);
        layout.setRight(buttonsLayout);
    }
}
