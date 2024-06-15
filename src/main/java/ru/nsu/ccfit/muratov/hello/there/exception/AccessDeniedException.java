package ru.nsu.ccfit.muratov.hello.there.exception;

public class AccessDeniedException extends Exception {
    public AccessDeniedException() {
        super();
    }

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(Throwable cause) {
        super(cause);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
