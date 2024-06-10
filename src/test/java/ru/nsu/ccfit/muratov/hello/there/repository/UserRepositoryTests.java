package ru.nsu.ccfit.muratov.hello.there.repository;

import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public UserEntity createDummyUser() throws ParseException {
        Role role = new Role();
        role.setName("USER");
        roleRepository.save(role);

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

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isGreaterThan(0);
        Assertions.assertThat(savedUser).isEqualTo(user);
    }

    @Test
    @DisplayName("Get user by ID")
    public void getUser() throws ParseException {
        UserEntity user = createDummyUser();

        userRepository.save(user);
        UserEntity dbUser = userRepository.getReferenceById(user.getId());

        Assertions.assertThat(dbUser).isNotNull().isEqualTo(user);
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

        Assertions.assertThat(updatedUser).isNotNull().isEqualTo(savedUser);
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
}
