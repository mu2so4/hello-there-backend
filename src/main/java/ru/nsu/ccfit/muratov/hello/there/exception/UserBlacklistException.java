package ru.nsu.ccfit.muratov.hello.there.exception;

public class UserBlacklistException extends BlacklistedException {
    public UserBlacklistException(String message) {
        super(message);
    }
}
