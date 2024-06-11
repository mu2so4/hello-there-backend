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
import ru.nsu.ccfit.muratov.hello.there.entity.Group;
import ru.nsu.ccfit.muratov.hello.there.entity.GroupBlacklist;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.entity.id.GroupBlacklistId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class GroupBlacklistRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupBlacklistRepository groupBlacklistRepository;

    private Group group;
    private GroupBlacklist blacklistRecord;

    @BeforeEach
    public void init() throws ParseException {
        UserEntity owner = createTestUser("mu2so4");
        group = createTestGroup(owner);
        UserEntity blocked = createTestUser("muso4");
        userRepository.saveAll(List.of(owner, blocked));
        groupRepository.save(group);
        blacklistRecord = createTestBlacklist(group, blocked);
    }

    @Test
    @DisplayName("Add to group blacklist")
    public void create() {


        var saved = groupBlacklistRepository.save(blacklistRecord);

        Assertions.assertThat(saved)
                .isNotNull()
                .isEqualTo(blacklistRecord);
    }

    @Test
    @DisplayName("Get group blacklist record by group and blocked user")
    public void getById() {
        groupBlacklistRepository.save(blacklistRecord);

        var saved = groupBlacklistRepository.getReferenceById(blacklistRecord.getId());

        Assertions.assertThat(saved)
                .isNotNull()
                .isEqualTo(blacklistRecord);
    }

    @Test
    @DisplayName("Remove from group blacklist")
    public void delete() {
        groupBlacklistRepository.save(blacklistRecord);

        groupBlacklistRepository.deleteById(blacklistRecord.getId());

        Assertions.assertThat(groupBlacklistRepository.existsById(blacklistRecord.getId()))
                .isFalse();
    }

    @Test
    @DisplayName("Get group's blacklist")
    public void getBlacklist() throws ParseException {
        UserEntity blocked2 = createTestUser("mu3po4");
        UserEntity blocked3 = createTestUser("ch3coomu");
        userRepository.saveAll(List.of(blocked2, blocked3));
        GroupBlacklist block2 = createTestBlacklist(group, blocked2);
        GroupBlacklist block3 = createTestBlacklist(group, blocked3);
        List<GroupBlacklist> blacklist = List.of(blacklistRecord, block2, block3);
        groupBlacklistRepository.saveAll(blacklist);

        var savedBlacklist = groupBlacklistRepository.findByGroup(group,
                PageRequest.of(0, 10, Sort.by("blockedUser.id")));

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

    private static Group createTestGroup(UserEntity owner) {
        Group group = new Group();
        group.setName("Dummy group");
        group.setDescription("Group for testing repository");
        group.setCreateTime(new Date());
        group.setOwner(owner);
        return group;
    }

    private static GroupBlacklist createTestBlacklist(Group blocker, UserEntity blocked) {
        GroupBlacklist blacklistRecord = new GroupBlacklist();
        blacklistRecord.setGroup(blocker);
        blacklistRecord.setBlockedUser(blocked);
        blacklistRecord.setId(new GroupBlacklistId(blocker.getId(), blocked.getId()));
        return blacklistRecord;
    }
}
