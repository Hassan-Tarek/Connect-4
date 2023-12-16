package org.connect4.networking.server;

import java.net.Socket;

public class GameSessionManager implements Runnable {
    private Socket firstClientSocket;
    private Socket secondClientSocket;

    public GameSessionManager(Socket firstClientSocket, Socket secondClientSocket) {
        this.firstClientSocket = firstClientSocket;
        this.secondClientSocket = secondClientSocket;
    }

    @Override
    public void run() {

    }
}
