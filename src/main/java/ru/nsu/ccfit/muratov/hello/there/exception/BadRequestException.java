package ru.nsu.ccfit.muratov.hello.there.exception;

public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(message);
    }
}
