package ru.nsu.ccfit.muratov.hello.there.exception;

public class MessageTimeoutExceededException extends AccessDeniedException {
    public MessageTimeoutExceededException(String message) {
        super(message);
    }
}
