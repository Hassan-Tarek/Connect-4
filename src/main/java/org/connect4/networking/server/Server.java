package org.connect4.networking.server;

public class Server {
    private static final int PORT = 4444;

    public static void main(String[] args) {
        ServerManager serverManager = new ServerManager(PORT);
        serverManager.start();
    }
}
