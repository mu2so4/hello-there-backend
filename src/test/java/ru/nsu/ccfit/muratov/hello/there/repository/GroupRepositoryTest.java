package ru.nsu.ccfit.muratov.hello.there.repository;

import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.nsu.ccfit.muratov.hello.there.entity.Group;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class GroupRepositoryTest {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;

    public Group createDummyGroup(UserEntity owner) {
        Group group = new Group();
        group.setName("Dummy group");
        group.setDescription("Group for testing repository");
        group.setCreateTime(new Date());
        userRepository.save(owner);
        group.setOwner(owner);
        return group;
    }

    public UserEntity createDummyUser() throws ParseException {
        UserEntity user = new UserEntity();
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

    @Test
    @DisplayName("Create group")
    public void create() throws ParseException {
        UserEntity user = createDummyUser();
        Group group = createDummyGroup(user);

        Group savedGroup = groupRepository.save(group);

        Assertions.assertThat(savedGroup)
                .isNotNull()
                .isEqualTo(group);
        Assertions.assertThat(savedGroup.getId()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Get group by id")
    public void getById() throws ParseException {
        UserEntity user = createDummyUser();
        Group group = createDummyGroup(user);
        groupRepository.save(group);

        Group foundGroup = groupRepository.getReferenceById(group.getId());

        Assertions.assertThat(foundGroup)
                .isNotNull()
                .isEqualTo(group);
    }

    @Test
    @DisplayName("Update group")
    public void update() throws ParseException {
        UserEntity user = createDummyUser();
        Group group = createDummyGroup(user);
        Group savedGroup = groupRepository.save(group);
        Integer id = group.getId();

        savedGroup.setName("osaehustoeush");
        savedGroup.setCreateTime(new Date());
        Group updatedGroup = groupRepository.save(savedGroup);

        Assertions.assertThat(updatedGroup)
                .isNotNull()
                .isEqualTo(group);
        Assertions.assertThat(updatedGroup.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Delete group")
    public void delete() throws ParseException {
        UserEntity user = createDummyUser();
        Group group = createDummyGroup(user);
        groupRepository.save(group);

        groupRepository.delete(group);

        Assertions.assertThatThrownBy(() -> groupRepository.getReferenceById(group.getId()))
                .hasCauseInstanceOf(EntityNotFoundException.class);
    }
}
