package org.connect4.server;

import org.connect4.server.core.ServerManager;
import org.connect4.server.exceptions.ServerStartFailureException;

public class Server {
    private static final int PORT = 4444;

    public static void main(String[] args) {
        try {
            ServerManager serverManager = new ServerManager(PORT);
            serverManager.start();
        } catch (ServerStartFailureException e) {
            System.err.println(e.getMessage());
        }
    }
}
