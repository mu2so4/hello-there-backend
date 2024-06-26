package ru.nsu.ccfit.muratov.hello.there.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import ru.nsu.ccfit.muratov.hello.there.dto.auth.RegistrationRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.blacklist.user.UserBlacklistRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.UserBlacklist;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.BadRequestException;
import ru.nsu.ccfit.muratov.hello.there.exception.UserNotFoundException;

public interface UserEntityService {
    UserEntity registerUser(RegistrationRequestDto form) throws BadRequestException;

    UserEntity getUserByUserDetails(UserDetails userDetails);
    UserEntity getById(Integer userId) throws UserNotFoundException;

    UserBlacklist addToBlacklist(UserEntity blocker, UserBlacklistRequestDto dto) throws BadRequestException, UserNotFoundException;
    void removeFromBlacklist(UserEntity blocker, Integer userId) throws UserNotFoundException;
    Page<UserBlacklist> getBlacklist(UserEntity blocker, Pageable pageable);

    boolean isBlacklistedByUser(UserEntity blocker, UserEntity blocked);
    boolean isSomeoneBlacklistedEachOther(UserEntity user1, UserEntity user2);
}
