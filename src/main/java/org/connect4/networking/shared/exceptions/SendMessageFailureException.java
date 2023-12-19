package org.connect4.networking.shared.exceptions;

import java.io.IOException;

public class SendMessageFailureException extends IOException {
    public SendMessageFailureException(String message) {
        super(message);
    }
}
