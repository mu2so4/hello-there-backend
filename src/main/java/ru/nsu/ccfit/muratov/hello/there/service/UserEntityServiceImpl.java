package ru.nsu.ccfit.muratov.hello.there.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.muratov.hello.there.entity.UserBlacklistId;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.repository.UserBlacklistRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.UserRepository;

@Service
public class UserEntityServiceImpl implements UserEntityService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserBlacklistRepository userBlacklistRepository;

    @Override
    public UserEntity getUserByUserDetails(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    @Override
    public boolean isBlacklistedByUser(UserEntity blocker, UserEntity blocked) {
        return userBlacklistRepository.existsById(new UserBlacklistId(blocker.getId(), blocked.getId()));
    }

    @Override
    public boolean isSomeoneBlacklistedEachOther(UserEntity user1, UserEntity user2) {
        return isBlacklistedByUser(user1, user2) || isBlacklistedByUser(user2, user1);
    }
}
