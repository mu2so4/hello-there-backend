package ru.nsu.ccfit.muratov.hello.there.service.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.nsu.ccfit.muratov.hello.there.dto.auth.RegistrationRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Role;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.BadRequestException;
import ru.nsu.ccfit.muratov.hello.there.repository.RoleRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.UserRepository;
import ru.nsu.ccfit.muratov.hello.there.service.impl.UserEntityServiceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegisterUserTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserEntityServiceImpl userService;
    
    private RegistrationRequestDto dto;
    private Role role;
    private Integer userId;

    @BeforeEach
    public void init() throws ParseException {
        final String ROLE_NAME = "USER";
        role = new Role(1, ROLE_NAME);
        userId = 1234;

        dto = createRegistrationRequest();
    }

    @Test
    @DisplayName("Create new user with valid data")
    public void create() throws BadRequestException {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserEntityServiceImpl(userRepository, null, roleRepository, passwordEncoder);
        when(userRepository.save(any(UserEntity.class))).thenAnswer((invocation) -> {
            UserEntity savedUser = invocation.getArgument(0, UserEntity.class);
            savedUser.setId(userId);
            return savedUser;
        });
        when(roleRepository.findByName("USER")).thenReturn(role);

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

    @Nested
    @DisplayName("Username parameter test")
    public class UsernameTest {
        @ParameterizedTest
        @ValueSource(strings = {"mu2so4", "Mu2SO4", "maxim_muratov", "_____"})
        @DisplayName("Create with valid username")
        public void createWithValidUsernames(String username) {
            dto.setUsername(username);
            when(userRepository.save(any(UserEntity.class))).thenAnswer((invocation) -> {
                UserEntity savedUser = invocation.getArgument(0, UserEntity.class);
                savedUser.setId(userId);
                return savedUser;
            });
            when(roleRepository.findByName("USER")).thenReturn(role);

            org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> userService.registerUser(dto));
        }

        @ParameterizedTest
        @ValueSource(ints = {4, 5, 6, 19, 20, 21})
        @DisplayName("Create with username having different lengths")
        public void createVarious(int usernameLength) {
            final int MIN_USERNAME_LENGTH = 5;
            final int MAX_USERNAME_LENGTH = 20;
            String username = "A".repeat(usernameLength);
            dto.setUsername(username);
            boolean isValidLength = (usernameLength >= MIN_USERNAME_LENGTH) && (usernameLength <= MAX_USERNAME_LENGTH);

            if (isValidLength) {
                org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> userService.registerUser(dto));
            } else {
                org.junit.jupiter.api.Assertions.assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            }
        }

        @Test
        @DisplayName("Create new user with null username")
        public void createWithNullUserName() {
            dto.setUsername(null);

            BadRequestException e =
                    org.junit.jupiter.api.Assertions.assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e)
                    .hasMessage("Username not set");
        }

        @Test
        @DisplayName("Create new user with too long username")
        public void createWithTooLongUserName() {
            dto.setUsername("A".repeat(25));

            BadRequestException e =
                    org.junit.jupiter.api.Assertions.assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e)
                    .hasMessage("Username too long");
        }

        @Test
        @DisplayName("Create new user with too short username")
        public void createWithTooShortUserName() {
            dto.setUsername("A");

            BadRequestException e =
                    org.junit.jupiter.api.Assertions.assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e)
                    .hasMessage("Username too short");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Мяу", "Максим", "maxim-muratov", "maybe?", "@mu2so4", "maxim muratov", "maxim.muratov"})
        @DisplayName("Create with username having illegal symbols")
        public void createWithIllegalSymbols(String username) {
            dto.setUsername(username);

            BadRequestException e =
                    org.junit.jupiter.api.Assertions.assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e)
                    .hasMessage("Illegal symbols in username");
        }


        @Test
        @DisplayName("Create with username that was taken")
        public void createWithTakenUsername() throws BadRequestException, ParseException {
            String takenUsername = "Mu2SO4";
            dto.setUsername(takenUsername);
            userService.registerUser(dto);
            RegistrationRequestDto nextDto = createRegistrationRequest();
            when(userRepository.existsByUsername(any(String.class))).thenReturn(true);

            BadRequestException e =
                    org.junit.jupiter.api.Assertions.assertThrows(BadRequestException.class, () -> userService.registerUser(nextDto));
            Assertions.assertThat(e)
                    .hasMessage("Username is already taken");
        }
    }

    private static RegistrationRequestDto createRegistrationRequest() throws ParseException {
        RegistrationRequestDto dto = new RegistrationRequestDto();
        dto.setUsername("mu2so4");
        dto.setPassword("1234");
        dto.setFirstName("Maxim");
        dto.setLastName("Muratov");
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        Date date = parser.parse("2002-01-01");
        dto.setBirthday(date);
        return dto;
    }
}
