package ru.nsu.ccfit.muratov.hello.there.security;

public interface TokenBlacklist {
    void addToBlacklist(String token);
    boolean isBlacklisted(String token);
}
