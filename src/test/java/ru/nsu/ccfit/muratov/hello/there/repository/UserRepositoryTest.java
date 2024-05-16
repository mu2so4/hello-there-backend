package ru.nsu.ccfit.muratov.hello.there.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;

import java.util.Optional;

@SpringBootTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @Test
    public void loadByUsername() {
        Optional<UserEntity> users = repository.findByUsername("mu2so4");
        System.out.println(users.orElseThrow(() -> new UsernameNotFoundException("user not found")));
    }
}
