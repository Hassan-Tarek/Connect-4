package org.connect4.client;

import org.connect4.client.exceptions.ServerConnectionFailureException;
import org.connect4.client.core.ClientManager;
import org.connect4.client.core.MessageHandler;
import org.connect4.game.logic.core.Move;
import org.connect4.game.networking.Message;
import org.connect4.game.networking.MessageType;
import org.connect4.game.networking.exceptions.ReceiveMessageFailureException;
import org.connect4.game.networking.exceptions.SendMessageFailureException;

import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 4444;

    public static void main(String[] args) {
        try {
            ClientManager clientManager = new ClientManager(SERVER_ADDRESS, PORT);

            System.out.println("Connected to server. Start sending messages:");
            Scanner scanner = new Scanner(System.in);
            Thread sendThread = new Thread(() -> {
                while (true) {
                    int column = scanner.nextInt();
                    Move move = new Move(null, column);
                    Message<Move> message = new Message<>(MessageType.MOVE, move);
                    try {
                        clientManager.sendMessage(message);
                    } catch (SendMessageFailureException e) {
                        scanner.close();
                        System.err.println(e.getMessage());
                    }
                }
            });
            sendThread.start();

            Thread receiveThread = new Thread(() -> {
                while (true) {
                    try {
                        Message<?> message = clientManager.getReceivedMessage();
                        MessageHandler.handleMessage(message);
                    } catch (ReceiveMessageFailureException e) {
                        System.err.println(e.getMessage());
                    }
                }
            });
            receiveThread.start();
        } catch (ServerConnectionFailureException e) {
            System.err.println(e.getMessage());
        }
    }
}
