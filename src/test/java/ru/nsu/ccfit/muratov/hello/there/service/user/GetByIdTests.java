package ru.nsu.ccfit.muratov.hello.there.service.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.UserNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.repository.RoleRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.UserRepository;
import ru.nsu.ccfit.muratov.hello.there.service.impl.UserEntityServiceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetByIdTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserEntityServiceImpl userService;

    private Integer userId;
    private UserEntity user;

    @BeforeEach
    public void init() throws ParseException {
        userId = 5;
        user = createTestUser(userId);
    }

    @Test
    @DisplayName("Get by ID if user exists")
    public void getById() throws UserNotFoundException {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserEntity savedUser = userService.getById(userId);

        Assertions.assertThat(savedUser)
                .isNotNull()
                .isEqualTo(user);
    }

    @Test
    @DisplayName("Get by ID if user does not exist")
    public void getByIdIfNotExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException e =
                org.junit.jupiter.api.Assertions.assertThrows(UserNotFoundException.class,
                        () -> userService.getById(userId));
        Assertions.assertThat(e)
                .hasMessage("User not found");
    }


    private static UserEntity createTestUser(Integer id) throws ParseException {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername("mu2so4");
        user.setPassword("1234");
        user.setFirstName("Maxim");
        user.setLastName("Muratov");
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        Date date = parser.parse("2002-01-01");
        user.setBirthday(date);
        user.setRegistrationTime(new Date());
        return user;
    }
}
