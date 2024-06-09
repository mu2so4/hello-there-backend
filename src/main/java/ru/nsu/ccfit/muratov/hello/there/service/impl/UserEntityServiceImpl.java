package ru.nsu.ccfit.muratov.hello.there.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.muratov.hello.there.dto.auth.RegistrationRequestDto;
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

@Service
public class UserEntityServiceImpl implements UserEntityService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserBlacklistRepository userBlacklistRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String USER_ROLE_NAME = "USER";

    @Override
    public UserEntity registerUser(RegistrationRequestDto form) {
        UserEntity user = new UserEntity();
        user.setUsername(form.getUsername());
        setPassword(user, form.getPassword());
        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        user.setRegistrationTime(new Date());
        user.setBirthday(form.getBirthday());
        user.setRoles(Collections.singleton(roleRepository.findByName(USER_ROLE_NAME)));
        return userRepository.save(user);
    }

    @Override
    public UserEntity getUserByUserDetails(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    @Override
    public UserEntity getById(Integer id) throws UserNotFoundException {
        if(!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        return userRepository.getReferenceById(id);
    }

    @Override
    public void addToBlacklist(UserEntity blocker, UserEntity blocked) throws BadRequestException {
        if(blocker.equals(blocked)) {
            throw new BadRequestException("Cannot add themselves to blacklist");
        }
        UserBlacklist userBlacklist = new UserBlacklist();
        userBlacklist.setId(new UserBlacklistId(blocker.getId(), blocked.getId()));
        userBlacklist.setBlocker(blocker);
        userBlacklist.setBlocked(blocked);
        userBlacklistRepository.save(userBlacklist);
    }

    @Override
    public void removeFromBlacklist(UserEntity blocker, UserEntity blocked) {
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

    private void setPassword(UserEntity user, String password) {
        user.setPassword(passwordEncoder.encode(password));
    }
}
