package org.connect4.server.exceptions;

public class ServerStartFailureException extends Exception {
    public ServerStartFailureException(String message) {
        super(message);
    }
}