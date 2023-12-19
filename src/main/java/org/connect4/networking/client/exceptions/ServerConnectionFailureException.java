package org.connect4.networking.client.exceptions;

import java.io.IOException;

public class ServerConnectionFailureException extends IOException {
    public ServerConnectionFailureException(String message) {
        super(message);
    }
}
