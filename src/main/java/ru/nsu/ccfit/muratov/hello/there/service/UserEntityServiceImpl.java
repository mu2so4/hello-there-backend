package ru.nsu.ccfit.muratov.hello.there.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.repository.UserRepository;

public class UserEntityServiceImpl implements UserEntityService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserEntity getUserByUserDetails(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }
}
