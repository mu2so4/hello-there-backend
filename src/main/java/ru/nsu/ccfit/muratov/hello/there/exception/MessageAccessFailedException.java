package ru.nsu.ccfit.muratov.hello.there.exception;

public class MessageAccessFailedException extends AccessDeniedException {
    public MessageAccessFailedException(String message) {
        super(message);
    }
}
