package ru.nsu.ccfit.muratov.hello.there.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.nsu.ccfit.muratov.hello.there.dto.auth.RegistrationRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Role;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.UserNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.repository.RoleRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.UserRepository;
import ru.nsu.ccfit.muratov.hello.there.service.impl.UserEntityServiceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserEntityServiceImpl userService;

    @Nested
    public class RegisterUserTest {
        @BeforeEach
        public void init() {
            passwordEncoder = new BCryptPasswordEncoder();
            userService = new UserEntityServiceImpl(userRepository, null, roleRepository, passwordEncoder);
        }

        @Test
        @DisplayName("Create new user")
        public void create() throws ParseException {
            final String ROLE_NAME = "USER";
            Role role = new Role(1, ROLE_NAME);
            Integer userId = 1234;
            when(userRepository.save(any(UserEntity.class))).thenAnswer((invocation) -> {
                UserEntity savedUser = invocation.getArgument(0, UserEntity.class);
                savedUser.setId(userId);
                return savedUser;
            });
            when(roleRepository.findByName("USER")).thenReturn(role);
            RegistrationRequestDto dto = new RegistrationRequestDto();
            dto.setUsername("mu2so4");
            dto.setPassword("1234");
            dto.setFirstName("Maxim");
            dto.setLastName("Muratov");
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            Date date = parser.parse("2002-01-01");
            dto.setBirthday(date);

            UserEntity user = userService.registerUser(dto);

            Assertions.assertThat(user).isNotNull();
            Assertions.assertThat(user.getId()).isEqualTo(userId);
            Assertions.assertThat(user.getUsername()).isEqualTo(dto.getUsername());
            Assertions.assertThat(user.getFirstName()).isEqualTo(dto.getFirstName());
            Assertions.assertThat(user.getLastName()).isEqualTo(dto.getLastName());
            Assertions.assertThat(user.getBirthday()).isEqualTo(dto.getBirthday());
            Assertions.assertThat(passwordEncoder.matches(dto.getPassword(), user.getPassword()))
                    .isTrue();
            Assertions.assertThat(user.getRoles())
                    .contains(role)
                    .hasSize(1);
        }
    }

    @Nested
    public class GetByIdTest {
        Integer userId = 5;
        UserEntity user;

        @BeforeEach
        public void init() throws ParseException {
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
