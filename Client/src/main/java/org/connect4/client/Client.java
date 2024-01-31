package org.connect4.client;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import org.connect4.client.core.ClientConnection;
import org.connect4.client.core.MessageHandler;
import org.connect4.client.core.MessageListener;
import org.connect4.client.core.MessageSender;
import org.connect4.client.exceptions.ServerConnectionFailureException;
import org.connect4.client.gui.controllers.GameViewController;
import org.connect4.client.gui.controllers.MainViewController;
import org.connect4.client.utils.Constants;

/**
 * The entry point class for the Connect-4 Client Application.
 * @author Hassan
 */
public class Client extends Application {
    /**
     * Starts the JavaFX application.
     *
     * @param stage the primary stage for the application.
     */
    @SuppressWarnings("BusyWait")
    @Override
    public void start(Stage stage) throws InterruptedException {
        ClientConnection clientConnection = new ClientConnection(Constants.SERVER_ADDRESS, Constants.SERVER_PORT);
        MessageSender messageSender = new MessageSender(clientConnection);
        MainViewController mainViewController = new MainViewController(stage, messageSender);
        GameViewController gameViewController = new GameViewController(stage, messageSender);
        MessageHandler messageHandler = new MessageHandler(clientConnection, messageSender, mainViewController, gameViewController);
        MessageListener messageListener = new MessageListener(clientConnection, messageHandler);

        Alert waitingToConnectToServerAlert = new Alert(Alert.AlertType.ERROR, "Waiting to connect to server.");
        while (!clientConnection.isConnected()) {
            try {
                clientConnection.connectToServer();

                if (clientConnection.isConnected()) {
                    waitingToConnectToServerAlert.close();
                }
            } catch (ServerConnectionFailureException e) {
                if (!waitingToConnectToServerAlert.isShowing()) {
                    waitingToConnectToServerAlert.show();
                }
                Thread.sleep(5000);
            }
        }

        mainViewController.showView();
        new Thread(messageListener).start();
    }

    /**
     * The main entry point of the Connect-4 client application.
     *
     * @param args the command-line arguments.
     */
    public static void main(String[] args) {
        launch();
    }
}
