package org.connect4.networking.client;

import org.connect4.networking.shared.Message;
import org.connect4.networking.shared.MessageType;

import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 4444;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        ClientManager clientManager = new ClientManager(SERVER_ADDRESS, PORT);
        clientManager.connectToServer();

        Scanner scanner = new Scanner(System.in);
        Thread sendThread = new Thread(() -> {
            while (true) {
                String input = scanner.nextLine();
                Message<String> message = new Message<>(MessageType.TEXT, input);
                clientManager.sendMessage(message);
            }
        });
        sendThread.start();

        Thread receiveThread = new Thread(() -> {
            while (true) {
                Message<String> message = (Message<String>) clientManager.getReceivedMessage();
                MessageHandler.handleMessage(message);
            }
        });
        receiveThread.start();
    }
}