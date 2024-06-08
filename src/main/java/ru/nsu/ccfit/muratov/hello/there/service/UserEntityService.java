package ru.nsu.ccfit.muratov.hello.there.service;

import org.springframework.security.core.userdetails.UserDetails;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;

public interface UserEntityService {
    UserEntity getUserByUserDetails(UserDetails userDetails);

    boolean isBlacklistedByUser(UserEntity blocker, UserEntity blocked);

    boolean isSomeoneBlacklistedEachOther(UserEntity user1, UserEntity user2);
}
