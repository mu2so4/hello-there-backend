package ru.nsu.ccfit.muratov.hello.there.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.repository.UserRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserEntityService userService;

    @Test
    public void loadByUsername() {
        Optional<UserEntity> users = repository.findByUsername("mu2so4");
        System.out.println(users.orElseThrow(() -> new UsernameNotFoundException("user not found")));
    }
}
