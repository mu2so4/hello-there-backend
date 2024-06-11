package ru.nsu.ccfit.muratov.hello.there.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.nsu.ccfit.muratov.hello.there.entity.UserBlacklist;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.entity.id.UserBlacklistId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserBlacklistRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserBlacklistRepository userBlacklistRepository;

    private UserEntity blocker;
    private UserBlacklist userBlacklist;

    @BeforeEach
    public void init() throws ParseException {
        blocker = createTestUser("mu2so4");
        UserEntity blocked = createTestUser("muso4");
        userRepository.saveAll(List.of(blocker, blocked));
        userBlacklist = createTestBlacklist(blocker, blocked);
    }

    @Test
    @DisplayName("Add to blacklist")
    public void create() {


        var saved = userBlacklistRepository.save(userBlacklist);

        Assertions.assertThat(saved)
                .isNotNull()
                .isEqualTo(userBlacklist);
    }

    @Test
    @DisplayName("Get blacklist record by blocker and blocked")
    public void getById() {
        userBlacklistRepository.save(userBlacklist);

        var saved = userBlacklistRepository.getReferenceById(userBlacklist.getId());

        Assertions.assertThat(saved)
                .isNotNull()
                .isEqualTo(userBlacklist);
    }

    @Test
    @DisplayName("Remove from blacklist")
    public void delete() {
        userBlacklistRepository.save(userBlacklist);

        userBlacklistRepository.deleteById(userBlacklist.getId());

        Assertions.assertThat(userBlacklistRepository.existsById(userBlacklist.getId()))
                .isFalse();
    }

    @Test
    @DisplayName("Get user's blacklist")
    public void getBlacklist() throws ParseException {
        UserEntity blocked2 = createTestUser("mu3po4");
        UserEntity blocked3 = createTestUser("ch3coomu");
        userRepository.saveAll(List.of(blocked2, blocked3));
        UserBlacklist block2 = createTestBlacklist(blocker, blocked2);
        UserBlacklist block3 = createTestBlacklist(blocker, blocked3);
        List<UserBlacklist> blacklist = List.of(userBlacklist, block2, block3);
        userBlacklistRepository.saveAll(blacklist);

        var savedBlacklist = userBlacklistRepository.findByBlocker(blocker,
                PageRequest.of(0, 10, Sort.by("blocked.id")));

        Assertions.assertThat(savedBlacklist)
                .isNotEmpty()
                .containsAll(blacklist);
    }

    private static UserEntity createTestUser(String username) throws ParseException {
        UserEntity user = new UserEntity();
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

    private static UserBlacklist createTestBlacklist(UserEntity blocker, UserEntity blocked) {
        UserBlacklist userBlacklist = new UserBlacklist();
        userBlacklist.setBlocker(blocker);
        userBlacklist.setBlocked(blocked);
        userBlacklist.setId(new UserBlacklistId(blocker.getId(), blocked.getId()));
        return userBlacklist;
    }
}
