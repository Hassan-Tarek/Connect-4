package org.connect4.game.networking.exceptions;

import java.io.IOException;

public class ReceiveMessageFailureException extends IOException {
    public ReceiveMessageFailureException(String message) {
        super(message);
    }
}
