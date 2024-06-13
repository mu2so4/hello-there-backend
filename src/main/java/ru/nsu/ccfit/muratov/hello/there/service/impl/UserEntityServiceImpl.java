package ru.nsu.ccfit.muratov.hello.there.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.muratov.hello.there.dto.auth.RegistrationRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.blacklist.user.UserBlacklistRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.UserBlacklist;
import ru.nsu.ccfit.muratov.hello.there.entity.id.UserBlacklistId;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.BadRequestException;
import ru.nsu.ccfit.muratov.hello.there.exception.UserNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.repository.RoleRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.UserBlacklistRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.UserRepository;
import ru.nsu.ccfit.muratov.hello.there.service.UserEntityService;

import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class UserEntityServiceImpl implements UserEntityService {
    private final UserRepository userRepository;
    private final UserBlacklistRepository userBlacklistRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String USER_ROLE_NAME = "USER";
    private static final int MIN_USERNAME_LENGTH = 5;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 10;

    public UserEntityServiceImpl(UserRepository userRepository, UserBlacklistRepository userBlacklistRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userBlacklistRepository = userBlacklistRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity registerUser(RegistrationRequestDto form) throws BadRequestException {
        String username = form.getUsername();
        validateUsername(username);

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        user.setRegistrationTime(new Date());
        user.setBirthday(form.getBirthday());
        setPassword(user, form.getPassword());
        user.setRoles(Collections.singleton(roleRepository.findByName(USER_ROLE_NAME))); //fixme to mutable set
        return userRepository.save(user);
    }

    @Override
    public UserEntity getUserByUserDetails(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    @Override
    public UserEntity getById(Integer userId) throws UserNotFoundException {
        return userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User not found")
        );
    }

    @Override
    public UserBlacklist addToBlacklist(UserEntity blocker, UserBlacklistRequestDto dto) throws BadRequestException, UserNotFoundException {
        UserEntity blocked = getById(dto.getId());
        if(blocker.equals(blocked)) {
            throw new BadRequestException("Cannot add themselves to blacklist");
        }
        UserBlacklist userBlacklist = new UserBlacklist();
        userBlacklist.setId(new UserBlacklistId(blocker.getId(), blocked.getId()));
        userBlacklist.setBlocker(blocker);
        userBlacklist.setBlocked(blocked);
        return userBlacklistRepository.save(userBlacklist);
    }

    @Override
    public void removeFromBlacklist(UserEntity blocker, Integer blockedId) throws UserNotFoundException {
        UserEntity blocked = getById(blockedId);
        userBlacklistRepository.deleteById(new UserBlacklistId(blocker.getId(), blocked.getId()));
        userRepository.save(blocker);
    }

    @Override
    public Page<UserBlacklist> getBlacklist(UserEntity blocker, Pageable pageable) {
        return userBlacklistRepository.findByBlocker(blocker, pageable);
    }

    @Override
    public boolean isBlacklistedByUser(UserEntity blocker, UserEntity blocked) {
        return userBlacklistRepository.existsById(new UserBlacklistId(blocker.getId(), blocked.getId()));
    }

    @Override
    public boolean isSomeoneBlacklistedEachOther(UserEntity user1, UserEntity user2) {
        return isBlacklistedByUser(user1, user2) || isBlacklistedByUser(user2, user1);
    }

    private void setPassword(UserEntity user, String password) throws BadRequestException {
        if(password == null) {
            throw new BadRequestException("Password not set");
        }
        if(password.length() < MIN_PASSWORD_LENGTH) {
            throw new BadRequestException("Password is too short");
        }
        String passwordLowerCase = password.toLowerCase(Locale.ROOT);
        if(passwordLowerCase.contains(user.getUsername().toLowerCase(Locale.ROOT)) ||
                passwordLowerCase.contains(user.getFirstName().toLowerCase(Locale.ROOT)) ||
                passwordLowerCase.contains(user.getLastName().toLowerCase(Locale.ROOT))) {
            throw new BadRequestException("Password must not contain user data");
        }
        user.setPassword(passwordEncoder.encode(password));
    }

    private void validateUsername(String username) throws BadRequestException {
        if(username == null) {
            throw new BadRequestException("Username not set");
        }
        if(!Pattern.matches("[A-Za-z0-9_]*", username)) {
            throw new BadRequestException("Illegal symbols in username");
        }
        if(username.length() > MAX_USERNAME_LENGTH) {
            throw new BadRequestException("Username too long");
        }
        if(username.length() < MIN_USERNAME_LENGTH) {
            throw new BadRequestException("Username too short");
        }
        if(userRepository.existsByUsernameIgnoreCase(username)) {
            throw new BadRequestException("Username is already taken");
        }
    }
}
