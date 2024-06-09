package ru.nsu.ccfit.muratov.hello.there.service;

import org.springframework.security.core.userdetails.UserDetails;
import ru.nsu.ccfit.muratov.hello.there.dto.auth.RegistrationRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.UserNotFoundException;

public interface UserEntityService {
    UserEntity registerUser(RegistrationRequestDto form);

    UserEntity getUserByUserDetails(UserDetails userDetails);

    UserEntity getById(Integer id) throws UserNotFoundException;

    boolean isBlacklistedByUser(UserEntity blocker, UserEntity blocked);

    boolean isSomeoneBlacklistedEachOther(UserEntity user1, UserEntity user2);
}
