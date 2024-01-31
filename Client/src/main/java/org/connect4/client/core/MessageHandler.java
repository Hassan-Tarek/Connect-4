package org.connect4.client.core;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.connect4.client.gui.controllers.GameViewController;
import org.connect4.client.gui.controllers.MainViewController;
import org.connect4.client.logger.ClientLogger;
import org.connect4.game.logic.core.Move;
import org.connect4.game.logic.enums.Color;
import org.connect4.game.networking.messaging.Message;
import org.connect4.game.networking.messaging.ServerMessageType;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The MessageHandler class processes incoming messages from the server and updates
 * the user interface accordingly.
 * @author Hassan
 */
public class MessageHandler {
    private static final ClientLogger LOGGER = ClientLogger.getLogger();

    private final ClientConnection clientConnection;
    private final MessageSender messageSender;
    private final MainViewController mainViewController;
    private final GameViewController gameViewController;
    private final Alert waitingForOpponentAlert;
    private final Alert opponentDisconnectedAlert;
    private final Alert winnerAlert;
    private final Alert rematchAlert;
    private final ExecutorService executorService;

    /**
     * Constructs a MessageHandler with the specified client connection, message sender,
     * main view controller, and game view controller.
     *
     * @param clientConnection   The client connection for handling disconnections.
     * @param messageSender      The message sender for sending requests (e.g., rematch requests).
     * @param mainViewController The main view controller for UI transitions.
     * @param gameViewController The game view controller for game-related UI updates.
     */
    public MessageHandler(ClientConnection clientConnection, MessageSender messageSender,
                          MainViewController mainViewController, GameViewController gameViewController) {
        this.clientConnection = clientConnection;
        this.messageSender = messageSender;
        this.mainViewController = mainViewController;
        this.gameViewController = gameViewController;
        this.executorService = Executors.newCachedThreadPool();

        this.waitingForOpponentAlert = createAlert(Alert.AlertType.INFORMATION, "Waiting for opponent...");
        this.opponentDisconnectedAlert = createAlert(Alert.AlertType.WARNING, "Opponent disconnected!");
        this.winnerAlert = createAlert(Alert.AlertType.INFORMATION, "");
        this.rematchAlert = createAlert(Alert.AlertType.CONFIRMATION, "Do you wanna play again?");
    }

    /**
     * Creates an alert dialog of the specified type with the given message.
     *
     * @param type    The type of the alert.
     * @param message The message to display in the alert.
     * @return The configured alert instance.
     */
    private Alert createAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        return alert;
    }

    /**
     * Handles the given message from the server by routing it to the appropriate handler
     * based on its type.
     *
     * @param message The message received from the server.
     * @param <T>     The type of the message payload.
     */
    @SuppressWarnings("unchecked")
    public <T> void handleMessage(Message<T> message) {
        executorService.submit(() -> {
            ServerMessageType serverMessageType = (ServerMessageType) message.getType();
            switch (serverMessageType) {
                case WAITING_FOR_OPPONENT -> handleWaitingForOpponentMessage();
                case GAME_STARTED -> handleGameStartedMessage();
                case COLOR -> handleColorMessage((Message<Color>) message);
                case PLAYER_SCORES -> handlePlayerScoresMessage((Message<int[]>) message);
                case MOVE -> handleMoveMessage((Message<Move>) message);
                case TEXT -> handleTextMessage((Message<String>) message);
                case PLAYER_TURN -> handlePlayerTurnMessage((Message<Color>) message);
                case GAME_OVER -> handleGameOverMessage((Message<Color>) message);
                case GAME_SESSION_ENDED -> handleGameSessionEndedMessage();
                case DISCONNECT_COMPLETED -> handleDisconnectCompletedMessage();
                case OPPONENT_DISCONNECTED -> handleOpponentDisconnectedMessage();
                case SERVER_STOPPED -> handleServerStoppedMessage();
            }
        });
    }

    /**
     * Handles a message indicating the client is waiting for an opponent to join.
     */
    private void handleWaitingForOpponentMessage() {
        Platform.runLater(waitingForOpponentAlert::showAndWait);
    }

    /**
     * Handles a message indicating that a game session has started.
     */
    private void handleGameStartedMessage() {
        Platform.runLater(() -> {
            waitingForOpponentAlert.close();
            mainViewController.closeView();
            gameViewController.showView();

            LOGGER.info("Game Started.");
        });
    }

    /**
     * Handles a message assigning the client's color for the game.
     *
     * @param colorMessage The message containing the assigned color.
     */
    private void handleColorMessage(Message<Color> colorMessage) {
        Platform.runLater(() -> {
            Color color = colorMessage.getPayload();
            javafx.scene.paint.Color assignedColor = color == Color.RED ? javafx.scene.paint.Color.RED : javafx.scene.paint.Color.YELLOW;
            gameViewController.setAssignedColor(assignedColor);
            gameViewController.setMyTurn(assignedColor == javafx.scene.paint.Color.RED);
        });
    }

    /**
     * Handles a message containing the updated player scores.
     *
     * @param scoresMessage The message containing player scores.
     */
    private void handlePlayerScoresMessage(Message<int[]> scoresMessage) {
        Platform.runLater(() -> {
            int[] scores = scoresMessage.getPayload();
            gameViewController.updatePlayersStatus(scores[0], scores[1]);

            LOGGER.info("RED: " + scores[0] + ", YELLOW: " + scores[1]);
        });
    }

    /**
     * Handles a move message, updating the game view with the move details.
     *
     * @param moveMessage The message containing the move.
     */
    private void handleMoveMessage(Message<Move> moveMessage) {
        Platform.runLater(() -> {
            Move move = moveMessage.getPayload();
            javafx.scene.paint.Color discColor = gameViewController.isMyTurn() ? gameViewController.getAssignedColor() :
                    (gameViewController.getAssignedColor() == javafx.scene.paint.Color.RED ? javafx.scene.paint.Color.YELLOW : javafx.scene.paint.Color.RED);
            gameViewController.dropDisc(discColor, move.getColumn());
            gameViewController.setMyTurn(!gameViewController.isMyTurn());
        });
    }

    /**
     * Handles a chat message, displaying the chat text in the game view.
     *
     * @param textMessage The message containing the chat text.
     */
    private void handleTextMessage(Message<String> textMessage) {
        Platform.runLater(() -> gameViewController.createChatBubble(textMessage.getPayload(), true));
    }

    /**
     * Handles a message indicating the current player's turn.
     *
     * @param playerTurnMessage The message containing the current player's color.
     */
    private void handlePlayerTurnMessage(Message<Color> playerTurnMessage) {
        Platform.runLater(() -> {
            Color currentPlayer = playerTurnMessage.getPayload();
            gameViewController.updateTurnIndicator(currentPlayer == Color.RED ? javafx.scene.paint.Color.RED : javafx.scene.paint.Color.YELLOW);
            LOGGER.info("It's " + currentPlayer + "'s turn!");
        });
    }

    /**
     * Handles a game-over message, displaying the winner and prompting for a rematch.
     *
     * @param gameOverMessage The message containing the winning player's color.
     */
    private void handleGameOverMessage(Message<Color> gameOverMessage) {
        Platform.runLater(() -> {
            Color winner = gameOverMessage.getPayload();
            System.out.println(winner);
            winnerAlert.setContentText(winner + " has won!");
            winnerAlert.showAndWait();

            Optional<ButtonType> result = rematchAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                messageSender.requestRematch();
                gameViewController.initialize();
                gameViewController.setEventHandlers();
            } else {
                gameViewController.closeView();
                mainViewController.showView();
            }
        });
    }

    /**
     * Handles a message indicating that the game session has ended.
     */
    private void handleGameSessionEndedMessage() {
        Platform.runLater(() -> {
            gameViewController.closeView();
            mainViewController.showView();
        });
    }

    /**
     * Handles a message indicating that the client has successfully disconnected.
     */
    private void handleDisconnectCompletedMessage() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownExecutor));
        Platform.runLater(() -> {
            gameViewController.closeView();
            mainViewController.closeView();
        });
    }

    /**
     * Handles a message indicating that the opponent has disconnected.
     */
    private void handleOpponentDisconnectedMessage() {
        Platform.runLater(() -> {
            opponentDisconnectedAlert.showAndWait();
            gameViewController.closeView();
            mainViewController.showView();
        });
    }

    /**
     * Handles a message indicating that the server has stopped.
     */
    private void handleServerStoppedMessage() {
        clientConnection.disconnect();
        Platform.runLater(() -> {
            gameViewController.closeView();
            mainViewController.closeView();
        });
    }

    /**
     * Shuts down the executor service.
     */
    private void shutdownExecutor() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
