package org.connect4.server.exceptions;

import java.io.IOException;

public class ServerStartFailureException extends IOException {
    public ServerStartFailureException(String message) {
        super(message);
    }
}
