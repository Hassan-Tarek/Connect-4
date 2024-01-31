package org.connect4.client.gui.controllers;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.connect4.client.core.MessageSender;
import org.connect4.client.gui.views.GameView;
import org.connect4.client.utils.Constants;
import org.connect4.game.logic.core.Move;

import java.util.Objects;

public class GameViewController implements BaseController {
    private final Stage stage;
    private final GameView gameView;
    private final MessageSender messageSender;

    private Color assignedColor;
    private boolean isMyTurn;

    public GameViewController(Stage stage, MessageSender messageSender) {
        this.stage = stage;
        this.gameView = new GameView();
        this.messageSender = messageSender;
        this.assignedColor = Color.BISQUE;
        this.isMyTurn = true;

        initialize();
        setEventHandlers();
    }

    @Override
    public void initialize() {
        // setup players icons
        setupPlayersIcons();

        // set players scores to zero
        updatePlayersStatus(0, 0);

        // fill the grid
        initializeBoard();
    }

    @Override
    public void setEventHandlers() {
        for (int col = 0; col < Constants.BOARD_COLS; col++) {
            int columnIndex = col;
            VBox columnBox = (VBox) gameView.getGridPane().getChildren().get(columnIndex);

            columnBox.setOnMouseEntered(mouseEvent -> {
                if (isMyTurn)
                    highlightColumn(columnIndex);
                else
                    resetColumnColor(columnIndex);
            });
            columnBox.setOnMouseExited(mouseEvent -> {
                if (isMyTurn)
                    resetColumnColor(columnIndex);
            });
            columnBox.setOnMouseClicked(mouseEvent -> {
                if (isMyTurn)
                    handleColumnClick(columnIndex);
                else
                    resetColumnColor(columnIndex);
            });
        }

        gameView.getChatTextField().textProperty().addListener((observableValue, oldValue, newValue) -> {
            boolean isEmpty = newValue.trim().isEmpty();
            gameView.getSendMessageButton().setDisable(isEmpty);
        });

        gameView.getSendMessageButton().setOnAction(event -> sendMessage());
        gameView.getChatTextField().setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });

        stage.setOnCloseRequest(event -> messageSender.requestLeaveGame());
    }

    @Override
    public void showView() {
        Scene scene = new Scene(gameView.getLayout(), Constants.GAME_VIEW_WIDTH, Constants.GAME_VIEW_HEIGHT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(Constants.STYLE_SHEET)).toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Game");
        stage.show();
    }

    @Override
    public void closeView() {
        stage.getScene().setRoot(new Pane());
        stage.close();
    }

    public Color getAssignedColor() {
        return assignedColor;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    public void setAssignedColor(Color assignedColor) {
        this.assignedColor = assignedColor;
    }

    public void setMyTurn(boolean myTurn) {
        isMyTurn = myTurn;
    }

    private void setupPlayersIcons() {
        Image redPlayerIcon = new Image(Objects.requireNonNull(GameView.class.getResource(Constants.RED_PLAYER_ICON)).toString());
        gameView.getRedPlayerImageView().setImage(redPlayerIcon);
        gameView.getRedPlayerImageView().setFitWidth(50);
        gameView.getRedPlayerImageView().setFitHeight(50);
        Image yellowPlayerIcon = new Image(Objects.requireNonNull(GameView.class.getResource(Constants.YELLOW_PLAYER_ICON)).toString());
        gameView.getYellowPlayerImageView().setImage(yellowPlayerIcon);
        gameView.getYellowPlayerImageView().setFitWidth(50);
        gameView.getYellowPlayerImageView().setFitHeight(50);
    }

    public void updatePlayersStatus(int redPlayerScore, int yellowPlayerScore) {
        gameView.getRedPlayerScore().setText(String.valueOf(redPlayerScore));
        gameView.getYellowPlayerScore().setText(String.valueOf(yellowPlayerScore));
    }

    public void updateTurnIndicator(Color color) {
        if (color == Color.RED) {
            gameView.getRedPlayerImageView().setFitWidth(70);
            gameView.getRedPlayerImageView().setFitHeight(70);
            gameView.getYellowPlayerImageView().setFitWidth(50);
            gameView.getYellowPlayerImageView().setFitHeight(50);
        } else {
            gameView.getYellowPlayerImageView().setFitWidth(70);
            gameView.getYellowPlayerImageView().setFitHeight(70);
            gameView.getRedPlayerImageView().setFitWidth(50);
            gameView.getRedPlayerImageView().setFitHeight(50);
        }
    }

    public void initializeBoard() {
        GridPane gridPane = gameView.getGridPane();
        gridPane.getChildren().clear();

        for (int col = 0; col < Constants.BOARD_COLS; col++) {
            VBox columnBox = new VBox();
//            columnBox.setStyle("-fx-background-color: lightgreen");
            columnBox.setSpacing(5);
            columnBox.setAlignment(Pos.TOP_CENTER);

            for (int row = 0; row < Constants.BOARD_ROWS; row++) {
                Circle slot = new Circle(Constants.DISC_RADIUS);
                slot.setFill(Color.WHITE);

                columnBox.getChildren().add(slot);
            }

            // Add each column VBox to the GridPane
            gridPane.add(columnBox, col, 0);
        }
    }

    public void dropDisc(Color discColor, int column) {
        int row = getRow(column);

        if (row < 0) {
            return;
        }

        // Create the animated disc
        Circle disc = new Circle(Constants.DISC_RADIUS, discColor);
        disc.setTranslateY(-Constants.TILE_SIZE);

        VBox columnBox = (VBox) gameView.getGridPane().getChildren().get(column);
        Circle targetDisc = (Circle) columnBox.getChildren().get(row);

        // Create an overlay StackPane to hold the animated disc
        StackPane overlay = new StackPane();
        overlay.getChildren().add(disc);

        // Add the overlay to the GridPane at the column position
        gameView.getGridPane().add(overlay, column, 0);

        // Set the overlay's alignment and position it correctly
        StackPane.setAlignment(disc, Pos.TOP_CENTER);
        overlay.setTranslateY(0);

        // Create the drop animation
        TranslateTransition dropAnimation = new TranslateTransition(Duration.seconds(1), disc);
        dropAnimation.setToY(row * Constants.TILE_SIZE);
        dropAnimation.setInterpolator(Interpolator.EASE_IN);

        dropAnimation.setOnFinished(event -> {
            targetDisc.setFill(discColor);
            gameView.getGridPane().getChildren().remove(overlay);
        });

        dropAnimation.play();
    }

    private int getRow(int column) {
        VBox columnBox = (VBox) gameView.getGridPane().getChildren().get(column);

        for (int row = Constants.BOARD_ROWS - 1; row >= 0; row--) {
            Circle circle = (Circle) columnBox.getChildren().get(row);

            if (circle.getFill().equals(Color.WHITE)) {
                return row;
            }
        }

        return -1;
    }

    public void createChatBubble(String message, boolean isFromOtherClient) {
        Text text = new Text(message);
        text.setFont(new Font(16));

        double textWidth = text.getBoundsInLocal().getWidth();
        double textHeight = text.getBoundsInLocal().getHeight();

        double bubbleWidth = textWidth + 20;
        double bubbleHeight = textHeight + 20;

        Rectangle bubble = new Rectangle(bubbleWidth, bubbleHeight);
        bubble.setArcWidth(15);
        bubble.setArcHeight(15);
        bubble.setFill(isFromOtherClient ? Color.LIGHTBLUE : Color.LIGHTGREEN);
        bubble.setStroke(Color.GRAY);

        TextFlow textFlow = new TextFlow(text);
        textFlow.setPadding(new Insets(5, 10, 5, 10));

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(bubble, textFlow);

        HBox hBox = new HBox();
        hBox.setAlignment(isFromOtherClient ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
        hBox.getChildren().add(stackPane);

        gameView.getChatLayout().getChildren().add(hBox);
    }

    private void handleColumnClick(int column) {
        if (column >= 0 && column < Constants.BOARD_COLS) {
            Move move = new Move(column);
            messageSender.sendMoveMessage(move);
        }
    }

    private void highlightColumn(int column) {
        VBox columnBox = (VBox) gameView.getGridPane().getChildren().get(column);
        columnBox.setStyle("-fx-background-color: darkblue");
    }

    private void resetColumnColor(int column) {
        VBox columnBox = (VBox) gameView.getGridPane().getChildren().get(column);
        columnBox.setStyle("-fx-background-color: none");
    }

    public void sendMessage() {
        String chatText = gameView.getChatTextField().getText();
        if (!chatText.isEmpty()) {
            messageSender.sendChatText(chatText);
            createChatBubble(chatText, false);
        }
        gameView.getChatTextField().clear();
    }
}
