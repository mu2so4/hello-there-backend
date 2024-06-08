package ru.nsu.ccfit.muratov.hello.there.exception;

public class GroupNotFoundException extends ResourceNotFoundException {
    public GroupNotFoundException(String message) {
        super(message);
    }
}
