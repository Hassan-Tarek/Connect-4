package org.connect4.client.gui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.connect4.client.utils.Constants;

public class GameView {
    private final BorderPane layout;
    private final GridPane gridPane;
    private final VBox chatLayout;
    private final ImageView redPlayerImageView;
    private final Label redPlayerScore;
    private final ImageView yellowPlayerImageView;
    private final Label yellowPlayerScore;
    private final TextField chatTextField;
    private final Button sendMessageButton;

    public GameView() {
        this.layout = new BorderPane();
        this.gridPane = new GridPane();
        this.chatLayout = new VBox(Constants.LAYOUT_PADDING);
        this.redPlayerImageView = new ImageView();
        this.redPlayerScore = new Label();
        this.yellowPlayerImageView = new ImageView();
        this.yellowPlayerScore = new Label();
        this.chatTextField = new TextField();
        this.sendMessageButton = new Button("Send");

        createLayout();
    }

    public BorderPane getLayout() {
        return layout;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public VBox getChatLayout() {
        return chatLayout;
    }

    public ImageView getRedPlayerImageView() {
        return redPlayerImageView;
    }

    public ImageView getYellowPlayerImageView() {
        return yellowPlayerImageView;
    }

    public Label getRedPlayerScore() {
        return redPlayerScore;
    }

    public Label getYellowPlayerScore() {
        return yellowPlayerScore;
    }

    public TextField getChatTextField() {
        return chatTextField;
    }

    public Button getSendMessageButton() {
        return sendMessageButton;
    }

    private void createLayout() {
        // Setup image views
        redPlayerImageView.setFitWidth(75);
        redPlayerImageView.setFitHeight(75);
        redPlayerImageView.setPreserveRatio(true);

        Label dashLabel = new Label("-");

        yellowPlayerImageView.setFitWidth(75);
        yellowPlayerImageView.setFitHeight(75);
        yellowPlayerImageView.setPreserveRatio(true);

        // players status layout
        HBox playersStatusLayout = new HBox(Constants.LAYOUT_PADDING);
        playersStatusLayout.setMinWidth(2 * Constants.GAME_VIEW_WIDTH / 3);
        playersStatusLayout.setMaxWidth(2 * Constants.GAME_VIEW_WIDTH / 3);
        playersStatusLayout.setMinHeight(150);
        playersStatusLayout.setAlignment(Pos.CENTER);
        playersStatusLayout.getChildren().addAll(redPlayerImageView, redPlayerScore, dashLabel, yellowPlayerScore, yellowPlayerImageView);

        // grid pane
        gridPane.setAlignment(Pos.BOTTOM_CENTER);
        gridPane.setMinWidth(2 * Constants.GAME_VIEW_WIDTH / 3);
        gridPane.setMaxWidth(2 * Constants.GAME_VIEW_WIDTH / 3);
        gridPane.setMinHeight(Constants.GAME_VIEW_HEIGHT - 150);
        gridPane.setMaxHeight(Constants.GAME_VIEW_HEIGHT - 150);
        gridPane.setAlignment(Pos.CENTER);
//        gridPane.setBackground(Background.fill(Color.BLUE));

        StackPane boardPane = new StackPane(gridPane);
        boardPane.setPrefWidth(2 * Constants.GAME_VIEW_WIDTH / 3);
        boardPane.setPrefHeight(Constants.GAME_VIEW_HEIGHT - 150);
        boardPane.setAlignment(Pos.CENTER);

        // left layout
        VBox leftLayout = new VBox();
        leftLayout.setMinWidth(2 * Constants.GAME_VIEW_WIDTH / 3);
        leftLayout.setMinHeight(Constants.GAME_VIEW_HEIGHT);
        leftLayout.setAlignment(Pos.CENTER);
        leftLayout.getChildren().addAll(playersStatusLayout, boardPane);

        // chat layout
        chatLayout.setPadding(new Insets(Constants.LAYOUT_PADDING));
        chatLayout.setMinWidth(Constants.GAME_VIEW_WIDTH / 3 - 30);
        chatLayout.setMaxWidth(Constants.GAME_VIEW_WIDTH / 3 - 30);
        chatLayout.setMinHeight(Constants.GAME_VIEW_HEIGHT - 150);
        chatLayout.setAlignment(Pos.BOTTOM_LEFT);

        // scroll pane for the chat layout
        ScrollPane scrollPane = new ScrollPane(chatLayout);
        scrollPane.setMinWidth(Constants.GAME_VIEW_WIDTH / 3 - 30);
        scrollPane.setMaxWidth(Constants.GAME_VIEW_WIDTH / 3 - 30);
        chatLayout.heightProperty().addListener((observable, oldValue, newValue) -> scrollPane.setVvalue(1.0));

        StackPane chatDisplayPane = new StackPane(scrollPane);
        chatDisplayPane.setMinHeight(Constants.GAME_VIEW_HEIGHT - 150);
        chatDisplayPane.setMaxHeight(Constants.GAME_VIEW_HEIGHT - 150);

        // text field
        chatTextField.setMinHeight(50);
        chatTextField.setMinWidth(2 * (Constants.GAME_VIEW_WIDTH / 3 - 50) / 3);
        chatTextField.setMaxWidth(2 * (Constants.GAME_VIEW_WIDTH / 3 - 50) / 3);
        chatTextField.setFocusTraversable(false);

        // button
        sendMessageButton.setMinWidth((Constants.GAME_VIEW_WIDTH / 3 - 50) / 3);
        sendMessageButton.setMaxWidth((Constants.GAME_VIEW_WIDTH / 3 - 50) / 3);
        sendMessageButton.setMinHeight(50);
        sendMessageButton.setDisable(true);
        sendMessageButton.setFocusTraversable(false);

        // chat input layout
        HBox chatInputLayout = new HBox(Constants.LAYOUT_PADDING);
        chatInputLayout.setMinWidth(Constants.GAME_VIEW_WIDTH / 3 - 30);
        chatInputLayout.setMaxWidth(Constants.GAME_VIEW_WIDTH / 3 - 30);
        chatInputLayout.setMinHeight(100);
        chatInputLayout.setAlignment(Pos.CENTER);
        chatInputLayout.getChildren().addAll(chatTextField, sendMessageButton);

        // right layout
        VBox rightLayout = new VBox();
        rightLayout.setMinWidth(Constants.GAME_VIEW_WIDTH / 3);
        rightLayout.setMinHeight(Constants.GAME_VIEW_HEIGHT);
        rightLayout.setAlignment(Pos.CENTER);
        rightLayout.getChildren().addAll(chatDisplayPane, chatInputLayout);

        // mainLayout
        layout.setLeft(leftLayout);
        layout.setRight(rightLayout);
    }
}
