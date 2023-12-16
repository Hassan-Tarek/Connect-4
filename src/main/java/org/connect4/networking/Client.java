package org.connect4.networking;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 4444;

    public static void main(String[] args) {
        ClientManager clientManager = new ClientManager(SERVER_ADDRESS, PORT);
        clientManager.connectToServer();
    }
}