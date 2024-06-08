package ru.nsu.ccfit.muratov.hello.there.exception;

public class BlacklistedException extends Exception {
    public BlacklistedException() {
        super();
    }

    public BlacklistedException(String message) {
        super(message);
    }

    public BlacklistedException(Throwable cause) {
        super(cause);
    }

    public BlacklistedException(String message, Throwable cause) {
        super(message, cause);
    }
}
