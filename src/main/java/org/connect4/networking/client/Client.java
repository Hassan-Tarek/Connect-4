package org.connect4.networking.client;

import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 4444;

    public static void main(String[] args) {
        ClientManager clientManager = new ClientManager(SERVER_ADDRESS, PORT);
        clientManager.connectToServer();

        Scanner scanner = new Scanner(System.in);
        Thread sendThread = new Thread(() -> {
            while (true) {
                String message = scanner.nextLine();
                clientManager.sendMessage(message);
            }
        });
        sendThread.start();

        Thread receiveThread = new Thread(() -> {
            while (true) {
                String message = (String) clientManager.getReceivedMessage();
                System.out.println("Message: " + message);
            }
        });
        receiveThread.start();
    }
}