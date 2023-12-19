package org.connect4.networking.shared.exceptions;

import java.io.IOException;

public class ReceiveMessageFailureException extends IOException {
    public ReceiveMessageFailureException(String message) {
        super(message);
    }
}
