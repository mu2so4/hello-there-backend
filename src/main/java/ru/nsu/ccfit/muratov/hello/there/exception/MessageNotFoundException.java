package ru.nsu.ccfit.muratov.hello.there.exception;

public class MessageNotFoundException extends ResourceNotFoundException {
    public MessageNotFoundException(String message) {
        super(message);
    }
}
