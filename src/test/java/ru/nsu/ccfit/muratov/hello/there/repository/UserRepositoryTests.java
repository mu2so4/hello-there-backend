package ru.nsu.ccfit.muratov.hello.there.repository;

import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.nsu.ccfit.muratov.hello.there.entity.Role;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    private final Role role = new Role();

    public UserRepositoryTests() {
        role.setName("USER");
    }

    public UserEntity createDummyUser() throws ParseException {
        UserEntity user = new UserEntity();
        user.setUsername("mu2so4");
        user.setPassword("1234");
        Set<Role> set = new HashSet<>();
        set.add(role);
        user.setRoles(set);
        user.setFirstName("Maxim");
        user.setLastName("Muratov");
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        Date date = parser.parse("2002-01-01");
        user.setBirthday(date);
        user.setRegistrationTime(new Date());
        return user;
    }

    @Test
    @DisplayName("Create user")
    public void createUser_thenSave() throws ParseException {
        UserEntity user = createDummyUser();

        UserEntity savedUser = userRepository.save(user);

        Assertions.assertThat(savedUser)
                .isNotNull()
                .isEqualTo(user);
        Assertions.assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Find user by ID")
    public void getUser() throws ParseException {
        UserEntity user = createDummyUser();

        userRepository.save(user);
        UserEntity dbUser = userRepository.getReferenceById(user.getId());

        Assertions.assertThat(dbUser)
                .isNotNull()
                .isEqualTo(user);
    }

    @Test
    @DisplayName("Update user")
    public void updateUser() throws ParseException {
        UserEntity user = createDummyUser();
        userRepository.save(user);
        Integer id = user.getId();

        var savedUser = userRepository.getReferenceById(id);
        String newFirstName = "Dmitry";
        String newLastName = "Udaltsov";
        savedUser.setFirstName(newFirstName);
        savedUser.setLastName(newLastName);
        var updatedUser = userRepository.save(savedUser);

        Assertions.assertThat(updatedUser)
                .isNotNull()
                .isEqualTo(savedUser);
        Assertions.assertThat(updatedUser.getId()).isEqualTo(id);
        Assertions.assertThat(updatedUser.getFirstName()).isEqualTo(newFirstName);
        Assertions.assertThat(updatedUser.getLastName()).isEqualTo(newLastName);
    }

    @Test
    @DisplayName("Delete user")
    public void deleteUser() throws ParseException {
        UserEntity user = createDummyUser();
        userRepository.save(user);

        userRepository.delete(user);

        Assertions.assertThatThrownBy(() -> userRepository.getReferenceById(user.getId()))
                .hasCauseInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Retrieve several users")
    public void getSeveralUsers() throws ParseException {
        UserEntity user1 = createDummyUser();
        UserEntity user2 = createDummyUser();
        user2.setUsername("muso4");
        UserEntity user3 = createDummyUser();
        user3.setUsername("def");
        List<UserEntity> users = List.of(user1, user2, user3);

        List<UserEntity> savedUsers = userRepository.saveAll(users);

        Assertions.assertThat(savedUsers)
                .isNotNull()
                .hasSize(users.size())
                .containsAll(users);
    }

    @Test
    @DisplayName("Find user by username")
    public void findByUsername() throws ParseException {
        UserEntity user = createDummyUser();
        String username = "CH3COOMu";
        String oldUsername = "saoehu";
        user.setUsername(username);
        userRepository.save(user);

        var foundUser = userRepository.findByUsername(username).orElseThrow();
        Assertions.assertThat(foundUser)
                .isNotNull()
                .isEqualTo(user);
        var noUser = userRepository.findByUsername(oldUsername);
        Assertions.assertThat(noUser.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Duplicate username")
    public void duplicateUsername() throws ParseException {
        UserEntity user1 = createDummyUser();
        UserEntity user2 = createDummyUser();
        user2.setLastName("Murashov");

        userRepository.save(user1);
        Assertions.assertThatThrownBy(() -> userRepository.save(user2))
                .hasCauseInstanceOf(ConstraintViolationException.class);
    }
}
