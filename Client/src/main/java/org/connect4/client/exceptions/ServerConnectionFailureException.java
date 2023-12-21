package org.connect4.client.exceptions;

import java.io.IOException;

public class ServerConnectionFailureException extends IOException {
    public ServerConnectionFailureException(String message) {
        super(message);
    }
}
