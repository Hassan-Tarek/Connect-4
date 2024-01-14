package org.connect4.server.core;

import javafx.util.Pair;
import org.connect4.game.ai.enums.AIType;
import org.connect4.game.logic.enums.GameType;
import org.connect4.game.networking.Message;
import org.connect4.game.networking.MessageType;
import org.connect4.game.networking.exceptions.ReceiveMessageFailureException;
import org.connect4.game.networking.exceptions.SendMessageFailureException;
import org.connect4.server.core.session.GameSession;
import org.connect4.server.core.session.MultiPlayerGameSession;
import org.connect4.server.core.session.SinglePlayerGameSession;
import org.connect4.server.exceptions.ServerStartFailureException;
import org.connect4.server.logging.ServerLogger;

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
    private static final ServerLogger logger = ServerLogger.getLogger();

    private final int port;
    private final List<Socket> waitingSockets;
    private final List<Socket> allAvailableSockets;
    private final List<GameSession> multiPlayerGameSessions;
    private final List<GameSession> singlePlayerGameSessions;
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
        this.multiPlayerGameSessions = new ArrayList<>();
        this.singlePlayerGameSessions = new ArrayList<>();
        this.socketStreamsMap = new HashMap<>();
        this.running = new AtomicBoolean(false);
    }

    /**
     * Gets the list of multi-player game sessions.
     * @return The list of multi-player game sessions.
     */
    public List<GameSession> getMultiPlayerGameSessions() {
        return multiPlayerGameSessions;
    }

    /**
     * Gets the list of single-player game sessions.
     * @return The list of single-player game sessions.
     */
    public List<GameSession> getSinglePlayerGameSessions() {
        return singlePlayerGameSessions;
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
    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(port);
            this.executorService = Executors.newCachedThreadPool();
            logger.finest("Server started!");

            while (running.get()) {
                try {
                    Socket acceptedClientSocket = serverSocket.accept();
                    allAvailableSockets.add(acceptedClientSocket);
                    socketStreamsMap.put(acceptedClientSocket,
                            new Pair<>(
                                    new ObjectOutputStream(acceptedClientSocket.getOutputStream()),
                                    new ObjectInputStream(acceptedClientSocket.getInputStream())));
                    logger.fine("New client accepted!");

                    Message<GameType> gameTypeMessage = (Message<GameType>) receiveMessage(acceptedClientSocket);
                    GameType gameType = gameTypeMessage.getPayload();

                    handleClientRequest(acceptedClientSocket, gameType);
                } catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        logger.severe("Server can't accept connection anymore: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            String message = "Failed to bind the server to port %d: ".formatted(port) + e.getMessage();
            logger.severe(message);
            throw new RuntimeException(new ServerStartFailureException(message));
        } finally {
            shutdown();
        }
    }

    /**
     * Handles the client request.
     */
    private void handleClientRequest(Socket clientSocket, GameType gameType) {
        switch (gameType) {
            case HUMAN_VS_HUMAN -> handleMultiPlayerGameSession(clientSocket);
            case HUMAN_VS_COMPUTER -> handleSinglePlayerGameSession(clientSocket);
            default -> logger.warning("Unknown game type received from client: " + clientSocket.getRemoteSocketAddress());
        }
    }

    /**
     * Handles the multi-player game session by matching the client with another waiting client.
     * @param clientSocket The client socket request multi-player game session.
     */
    private void handleMultiPlayerGameSession(Socket clientSocket) {
        if (!waitingSockets.isEmpty()) {
            Socket oppositeClientSocket = waitingSockets.remove(0);
            startMultiPlayerGameSession(clientSocket, oppositeClientSocket);
        } else {
            waitingSockets.add(clientSocket);
        }
    }

    /**
     * Handles the single-player game session by receiving an AI type and starts a game session with that AI.
     * @param clientSocket The client socket request single-player game session.
     */
    @SuppressWarnings("unchecked")
    private void handleSinglePlayerGameSession(Socket clientSocket) {
        try {
            Message<AIType> aiTypeMessage = (Message<AIType>) receiveMessage(clientSocket);
            AIType aiType = aiTypeMessage.getPayload();
            startSinglePlayerGameSession(clientSocket, aiType);
        } catch (ReceiveMessageFailureException e) {
            logger.severe("Failed to receive ai type message: " + e.getMessage());
        }
    }

    /**
     * Starts a multi-player game session with the two given client sockets.
     * @param firstClientSocket  The socket of the first client.
     * @param secondClientSocket The socket of the second client.
     */
    private void startMultiPlayerGameSession(Socket firstClientSocket, Socket secondClientSocket) {
        // Start a new game session
        GameSession gameSession = new MultiPlayerGameSession(this, firstClientSocket, secondClientSocket);
        addGameSession(gameSession);
    }

    /**
     * Starts a single-player game session with the given client and AI type.
     * @param clientSocket The socket of the client.
     * @param aiType The type of AI the client will play against.
     */
    private void startSinglePlayerGameSession(Socket clientSocket, AIType aiType) {
        // Start a new game session
        GameSession gameSession = new SinglePlayerGameSession(this, clientSocket, aiType);
        addGameSession(gameSession);
    }


    /**
     * Adds the given game session to the list of sessions and submits it to the executor service for execution.
     * @param gameSession The game session to be added and executed.
     */
    private void addGameSession(GameSession gameSession) {
        if (gameSession instanceof MultiPlayerGameSession) {
            multiPlayerGameSessions.add(gameSession);
        } else if (gameSession instanceof SinglePlayerGameSession) {
            singlePlayerGameSessions.add(gameSession);
        }
        executorService.submit(gameSession);

        logger.fine("New GameSession started!");
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
            String errorMessage = "Failed to send message to a client: " + e.getMessage();
            logger.severe(errorMessage);
            throw new SendMessageFailureException(errorMessage);
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
            String errorMessage = "Failed to receive message from a client: " + e.getMessage();
            logger.severe(errorMessage);
            throw new ReceiveMessageFailureException(errorMessage);
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
            logger.severe("Failed to close client socket: " + e.getMessage());
        }
    }

    /**
     * Stops the server.
     */
    public void shutdown() {
        if (running.compareAndSet(true, false)) {
            try {
                for (Socket socket : allAvailableSockets) {
                    sendMessage(socket, new Message<>(MessageType.SERVER_STOPPED, null));
                    closeStreams(socket);
                    closeSocket(socket);
                }

                for (GameSession session : multiPlayerGameSessions) {
                    session.shutdown();
                }

                for (GameSession session : singlePlayerGameSessions) {
                    session.shutdown();
                }

                executorService.shutdownNow();
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }

                logger.fine("Server stopped!");
            } catch (IOException e) {
                logger.severe("Failed to close server: " + e.getMessage());
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
            logger.severe("Failed to close streams: " + e.getMessage());
        }
    }

    /**
     * Cleans up the resources.
     */
    private void cleanup() {
        waitingSockets.clear();
        allAvailableSockets.clear();
        multiPlayerGameSessions.clear();
        singlePlayerGameSessions.clear();
        socketStreamsMap.clear();
    }
}
