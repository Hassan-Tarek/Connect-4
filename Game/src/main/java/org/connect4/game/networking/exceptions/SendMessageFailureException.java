package org.connect4.game.networking.exceptions;

import java.io.IOException;

public class SendMessageFailureException extends IOException {
    public SendMessageFailureException(String message) {
        super(message);
    }
}
