package ru.nsu.ccfit.muratov.hello.there.service.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nsu.ccfit.muratov.hello.there.dto.blacklist.user.UserBlacklistRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.UserBlacklist;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.BadRequestException;
import ru.nsu.ccfit.muratov.hello.there.exception.UserNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.repository.UserBlacklistRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.UserRepository;
import ru.nsu.ccfit.muratov.hello.there.service.impl.UserEntityServiceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddToBlacklistTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserBlacklistRepository userBlacklistRepository;

    @InjectMocks
    private UserEntityServiceImpl userService;

    private UserEntity blocker;
    private UserEntity blocked;
    UserBlacklistRequestDto dto;

    @BeforeEach
    public void init() throws ParseException {
        blocker = createTestUser("mu2so4", 1);
        blocked = createTestUser("muso4", 2);
        dto = new UserBlacklistRequestDto();
        dto.setId(blocked.getId());
    }

    @Test
    @DisplayName("Add to blacklist with valid values")
    public void addWithValidValue() throws UserNotFoundException, BadRequestException {

        when(userRepository.findById(blocked.getId())).thenReturn(Optional.of(blocked));
        when(userBlacklistRepository.save(any(UserBlacklist.class))).then(invocation -> invocation.getArgument(0));

        UserBlacklist userBlacklist = userService.addToBlacklist(blocker, dto);

        Assertions.assertThat(userBlacklist.getBlocker()).isEqualTo(blocker);
        Assertions.assertThat(userBlacklist.getBlocked()).isEqualTo(blocked);
    }

    @Test
    @DisplayName("Add to blacklist themselves")
    public void addBlockerBlockedSame() {
        dto.setId(blocker.getId());
        when(userRepository.findById(blocker.getId())).thenReturn(Optional.of(blocker));

        BadRequestException e = assertThrows(BadRequestException.class, () -> userService.addToBlacklist(blocker, dto));
        Assertions.assertThat(e).hasMessage("Cannot add themselves to blacklist");
    }

    @Test
    @DisplayName("Add to blacklist non-existing user")
    public void addNonExistingUser() {
        when(userRepository.findById(dto.getId())).thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> userService.addToBlacklist(blocker, dto));
        Assertions.assertThat(e).hasMessage("User not found");
    }

    private static UserEntity createTestUser(String username, Integer userId) throws ParseException {
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername(username);
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
