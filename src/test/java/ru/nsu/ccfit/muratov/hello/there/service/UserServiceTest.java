package ru.nsu.ccfit.muratov.hello.there.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.nsu.ccfit.muratov.hello.there.dto.auth.RegistrationRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Role;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.repository.RoleRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.UserRepository;
import ru.nsu.ccfit.muratov.hello.there.service.impl.UserEntityServiceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserEntityServiceImpl userService;

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
        when(passwordEncoder.encode(any(CharSequence.class)))
                .then((invocation) -> invocation.getArgument(0, String.class));
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
        Assertions.assertThat(user.getPassword()).isEqualTo(dto.getPassword()); //fixme
        Assertions.assertThat(user.getRoles())
                .contains(role)
                .hasSize(1);
    }
}
