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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        public void createWithValidValue(String username) {
            dto.setUsername(username);
            when(userRepository.save(any(UserEntity.class))).thenAnswer((invocation) -> {
                UserEntity savedUser = invocation.getArgument(0, UserEntity.class);
                savedUser.setId(userId);
                return savedUser;
            });
            when(roleRepository.findByName("USER")).thenReturn(role);

            assertDoesNotThrow(() -> userService.registerUser(dto));
        }

        @ParameterizedTest
        @ValueSource(ints = {4, 5, 6, 19, 20, 21})
        @DisplayName("Create with username having different lengths")
        public void createWithVariousLengths(int usernameLength) {
            final int MIN_USERNAME_LENGTH = 5;
            final int MAX_USERNAME_LENGTH = 20;
            String username = "A".repeat(usernameLength);
            dto.setUsername(username);
            boolean isValidLength = (usernameLength >= MIN_USERNAME_LENGTH) && (usernameLength <= MAX_USERNAME_LENGTH);

            if (isValidLength) {
                assertDoesNotThrow(() -> userService.registerUser(dto));
            } else {
                assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            }
        }

        @Test
        @DisplayName("Create new user with null username")
        public void createWithNull() {
            dto.setUsername(null);

            BadRequestException e = assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e).hasMessage("Username not set");
        }

        @Test
        @DisplayName("Create new user with too long username")
        public void createWithTooLong() {
            dto.setUsername("A".repeat(25));

            BadRequestException e = assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e).hasMessage("Username too long");
        }

        @Test
        @DisplayName("Create new user with too short username")
        public void createWithTooShort() {
            dto.setUsername("A");

            BadRequestException e = assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e).hasMessage("Username too short");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Мяу", "Максим", "maxim-muratov", "maybe?", "@mu2so4", "maxim muratov", "maxim.muratov"})
        @DisplayName("Create with username having illegal symbols")
        public void createWithIllegalSymbols(String username) {
            dto.setUsername(username);

            BadRequestException e = assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e).hasMessage("Illegal symbols in username");
        }


        @Test
        @DisplayName("Create with username that was taken")
        public void createWithTaken() throws BadRequestException, ParseException {
            String takenUsername = "Mu2SO4";
            dto.setUsername(takenUsername);
            userService.registerUser(dto);
            RegistrationRequestDto nextDto = createRegistrationRequest();
            when(userRepository.existsByUsernameIgnoreCase(any(String.class))).thenReturn(true);

            BadRequestException e = assertThrows(BadRequestException.class, () -> userService.registerUser(nextDto));
            Assertions.assertThat(e).hasMessage("Username is already taken");
        }
    }

    @Nested
    @DisplayName("Password parameter test")
    public class PasswordTest {
        //meaning of the emoji password: "It's over, Anakin! I have the high ground!"
        @ParameterizedTest
        @ValueSource(strings = {"veryDifficultPassword", "Невероятно сложный пароль", "Passw0RD.?", "🌐🛑🥷🤗⬆️🌋"})
        @DisplayName("Create with valid password")
        public void createWithValidValue(String password) {
            dto.setPassword(password);

            assertDoesNotThrow(() -> userService.registerUser(dto));
        }

        @Test
        @DisplayName("Create with null password")
        public void createWithNull() {
            dto.setPassword(null);

            BadRequestException e = assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e).hasMessage("Password not set");
        }

        @Test
        @DisplayName("Create with too short password")
        public void createWithTooShort() {
            dto.setPassword("1234");

            BadRequestException e = assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e).hasMessage("Password is too short");
        }

        @ParameterizedTest
        @ValueSource(ints = {9, 10, 11})
        @DisplayName("Create with password with different lengths")
        public void createWithVariousLengths(int passwordLength) {
            final int MIN_PASSWORD_LENGTH = 10;
            String password = "A".repeat(passwordLength);
            dto.setPassword(password);

            if(passwordLength >= MIN_PASSWORD_LENGTH) {
                assertDoesNotThrow(() -> userService.registerUser(dto));
            }
            else {
                BadRequestException e = assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
                Assertions.assertThat(e).hasMessage("Password is too short");
            }
        }

        /*
        @ParameterizedTest
        @ValueSource(strings = {"qwertyuiop", "1234567890", "PoIuYtReWq", "AAAAAAAAAAAAAAA", "abcdefghij"})
        @DisplayName("Create with too weak password")
        public void createWithTooWeakPassword(String password) {
            dto.setPassword(password);

            BadRequestException e =
                    assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e)
                    .hasMessage("Password is too weak");
        }
        */

        @ParameterizedTest
        @ValueSource(strings = {"000mu2so4000", "maximus1234", "Chel2Muratov"})
        @DisplayName("Create password containing user data")
        public void createWithPasswordContainingUserData(String password) {
            dto.setPassword(password);

            BadRequestException e = assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e).hasMessage("Password must not contain user data");
        }

        @ParameterizedTest
        @ValueSource(strings = {"Максимус1234", "Chel2Муратов"})
        @DisplayName("Create password containing user data in cyrillic")
        public void createWithPasswordContainingUserDataCyrillic(String password) {
            dto.setPassword(password);
            dto.setFirstName("Максим");
            dto.setLastName("Муратов");

            BadRequestException e = assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e).hasMessage("Password must not contain user data");
        }
    }

    @Nested
    public class FirstNameTest {
        @ParameterizedTest
        @ValueSource(strings = {"Maxim", "Максим", "Ян", "Si", "макс", "john", "Алёна", "Ёдгор", "Я", "RЯ"})
        @DisplayName("Create with valid first name")
        public void createWithValidValue(String firstName) {
            dto.setFirstName(firstName);

            assertDoesNotThrow(() -> userService.registerUser(dto));
        }

        @ParameterizedTest
        @ValueSource(ints = {19, 20, 21})
        @DisplayName("Create with last name with different lengths")
        public void createWithVariousLengths(int firstNameLength) {
            final int MAX_FIRST_NAME_LENGTH = 20;
            dto.setFirstName("A".repeat(firstNameLength));

            if(firstNameLength <= MAX_FIRST_NAME_LENGTH) {
                assertDoesNotThrow(() -> userService.registerUser(dto));
            }
            else {
                assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            }
        }

        @Test
        @DisplayName("Create with null first name")
        public void createWithNull() {
            dto.setFirstName(null);

            BadRequestException e = assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e).hasMessage("First name not set");
        }

        @Test
        @DisplayName("Create with empty first name")
        public void createWithEmpty() {
            dto.setFirstName("");

            BadRequestException e = assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e).hasMessage("First name not set");
        }

        @Test
        @DisplayName("Create with too long first name")
        public void createWithTooLong() {
            dto.setFirstName("A".repeat(30));

            BadRequestException e = assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e).hasMessage("First name too long");
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "_Максим_", "Максим\n", "Максим?", "maxim_", "Ma-xim", "\t"})
        @DisplayName("Create with first name having illegal symbols")
        public void createWithIllegalSymbols(String username) {
            dto.setFirstName(username);

            BadRequestException e = assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e).hasMessage("First name contains illegal symbols");
        }
    }

    @Nested
    public class LastNameTest {
        @ParameterizedTest
        @ValueSource(strings = {"Муратов", "Muratov", "Ohm", "muratov", "Селезнёва", "Ёдгорова", "Я", "RЯ", "Муратов-Иванов"})
        @DisplayName("Create with valid last name")
        public void createWithValidValue(String lastName) {
            dto.setLastName(lastName);

            assertDoesNotThrow(() -> userService.registerUser(dto));
        }

        @ParameterizedTest
        @ValueSource(ints = {19, 20, 21})
        @DisplayName("Create with last name with different lengths")
        public void createWithVariousLengths(int lastNameLength) {
            final int MAX_FIRST_NAME_LENGTH = 20;
            dto.setLastName("A".repeat(lastNameLength));

            if(lastNameLength <= MAX_FIRST_NAME_LENGTH) {
                assertDoesNotThrow(() -> userService.registerUser(dto));
            }
            else {
                assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            }
        }

        @Test
        @DisplayName("Create with null last name")
        public void createWithNull() {
            dto.setLastName(null);

            BadRequestException e = assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e).hasMessage("Last name not set");
        }

        @Test
        @DisplayName("Create with empty last name")
        public void createWithEmpty() {
            dto.setLastName("");

            BadRequestException e = assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e).hasMessage("Last name not set");
        }

        @Test
        @DisplayName("Create with too long last name")
        public void createWithTooLong() {
            dto.setLastName("A".repeat(30));

            BadRequestException e = assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e).hasMessage("Last name too long");
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "_Муратов_", "Муратов\n", "Муратов?", "muratov_", "\t"})
        @DisplayName("Create with last name having illegal symbols")
        public void createWithIllegalSymbols(String username) {
            dto.setLastName(username);

            BadRequestException e = assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
            Assertions.assertThat(e).hasMessage("Last name contains illegal symbols");
        }
    }

    private static RegistrationRequestDto createRegistrationRequest() throws ParseException {
        RegistrationRequestDto dto = new RegistrationRequestDto();
        dto.setUsername("mu2so4");
        dto.setPassword("veryDifficultPassword");
        dto.setFirstName("Maxim");
        dto.setLastName("Muratov");
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        Date date = parser.parse("2002-01-01");
        dto.setBirthday(date);
        return dto;
    }
}
