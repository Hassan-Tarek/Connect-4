package org.connect4.server.core;

import javafx.util.Pair;
import org.connect4.game.networking.Message;
import org.connect4.game.networking.exceptions.ReceiveMessageFailureException;
import org.connect4.game.networking.exceptions.SendMessageFailureException;
import org.connect4.server.exceptions.ServerStartFailureException;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class that represents the server for Connect-4 Game.
 * @author Hassan
 */
public class ServerManager implements Runnable {
    private final int port;
    private final List<Socket> waitingSockets;
    private final List<Socket> allAvailableSockets;
    private final List<GameSession> gameSessions;
    private final Map<Socket, Pair<ObjectOutputStream, ObjectInputStream>> socketStreamsMap;
    private final AtomicBoolean running;

    private ServerSocket serverSocket;
    private ExecutorService executorService;

    /**
     * Constructs a new ServerManager with the specified port number.
     * @param port The port number to bind the server to.
     */
    public ServerManager(int port) {
        this.port = port;
        this.waitingSockets = new ArrayList<>();
        this.allAvailableSockets = new ArrayList<>();
        this.gameSessions = new ArrayList<>();
        this.socketStreamsMap = new HashMap<>();
        this.running = new AtomicBoolean(false);
    }

    /**
     * Gets the list of game sessions.
     * @return The list of game sessions.
     */
    public List<GameSession> getGameSessions() {
        return gameSessions;
    }

    /**
     * Gets the list of waiting client sockets.
     * @return The list of waiting client sockets.
     */
    public List<Socket> getWaitingSockets() {
        return waitingSockets;
    }

    /**
     * Checks whether the server is running or not.
     * @return true if the server is running, false otherwise.
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * Starts running the server.
     */
    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(port);
            this.executorService = Executors.newCachedThreadPool();
            System.out.println("Server started...");

            while (running.get()) {
                acceptNewClient();

                if (waitingSockets.size() >= 2) {
                    startNewGameSession();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(new ServerStartFailureException("Failed to bind the server to port %d: ".formatted(port) + e.getMessage()));
        } finally {
            shutdown();
        }
    }

    /**
     * Accepts new client.
     */
    private void acceptNewClient() {
        try {
            Socket acceptedClientSocket = serverSocket.accept();
            waitingSockets.add(acceptedClientSocket);
            allAvailableSockets.add(acceptedClientSocket);
            socketStreamsMap.put(acceptedClientSocket,
                    new Pair<>(
                            new ObjectOutputStream(acceptedClientSocket.getOutputStream()),
                            new ObjectInputStream(acceptedClientSocket.getInputStream())));

            System.out.println("New client accepted!");
        } catch (IOException e) {
            if (!serverSocket.isClosed()) {
                System.err.println("Server can't accept connection anymore: " + e.getMessage());
            }
        }
    }

    /**
     * Starts new game session.
     */
    private void startNewGameSession() {
        Socket firstClientSocket = waitingSockets.remove(0);
        Socket secondClientSocket = waitingSockets.remove(0);

        // Start a new game session for these two clients
        GameSession gameSession = new GameSession(this, firstClientSocket, secondClientSocket);
        gameSessions.add(gameSession);
        executorService.submit(gameSession);

        System.out.println("New GameSession started!");
    }

    /**
     * Starts the server.
     */
    public void start() {
        if (running.compareAndSet(false, true)) {
            Thread serverThread = new Thread(this);
            serverThread.start();
        }
    }

    /**
     * Sends a message to the receiver's output stream.
     * @param socket The socket to which the message will be sent.
     * @param message The message to be sent.
     * @throws SendMessageFailureException If failed to send the message.
     */
    public void sendMessage(Socket socket, Message<?> message) throws SendMessageFailureException {
        try {
            ObjectOutputStream out = socketStreamsMap.get(socket).getKey();
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            throw new SendMessageFailureException("Failed to send message to a client: " + e.getMessage());
        }
    }

    /**
     * Receives a message from sender's input stream.
     * @param socket The socket from which the message will be received.
     * @return The received message.
     * @throws ReceiveMessageFailureException If failed to receive the message.
     */
    public Message<?> receiveMessage(Socket socket) throws ReceiveMessageFailureException {
        try {
            ObjectInputStream in = socketStreamsMap.get(socket).getValue();
            return (Message<?>) in.readObject();
        } catch (EOFException e) {
            return null;
        } catch (ClassNotFoundException | IOException e) {
            throw new ReceiveMessageFailureException("Failed to receive message from a client: " + e.getMessage());
        }
    }

    /**
     * Closes a socket.
     * @param socket The socket to be closed.
     */
    public void closeSocket(Socket socket) {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Failed to close client socket: " + e.getMessage());
        }
    }

    /**
     * Stops the server.
     */
    public void shutdown() {
        if (running.compareAndSet(true, false)) {
            try {
                for (Socket socket : allAvailableSockets) {
                    closeStreams(socket);
                    closeSocket(socket);
                }

                for (GameSession session : gameSessions) {
                    session.shutdown();
                }

                executorService.shutdownNow();
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }

                System.out.println("Server stopped...");
            } catch (IOException e) {
                System.err.println("Failed to close server: " + e.getMessage());
            } finally {
                cleanup();
            }
        }
    }

    /**
     * Closes output/input streams of a specific socket.
     * @param socket The socket whose output/input streams will be closed.
     */
    private void closeStreams(Socket socket) {
        ObjectOutputStream out = socketStreamsMap.get(socket).getKey();
        ObjectInputStream in = socketStreamsMap.get(socket).getValue();
        try {
            out.close();
            in.close();
        } catch (IOException e) {
            System.err.println("ERROR: Failed to close streams: " + e.getMessage());
        }
    }

    /**
     * Cleans up the resources.
     */
    private void cleanup() {
        waitingSockets.clear();
        allAvailableSockets.clear();
        gameSessions.clear();
        socketStreamsMap.clear();
    }
}
