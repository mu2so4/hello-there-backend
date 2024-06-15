package ru.nsu.ccfit.muratov.hello.there.exception;

public class GroupAdminAccessDeniedException extends AccessDeniedException {
    public GroupAdminAccessDeniedException(String message) {
        super(message);
    }
}
