package ru.nsu.ccfit.muratov.hello.there.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.nsu.ccfit.muratov.hello.there.entity.Role;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RoleRepositoryTests {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Read roles")
    public void readRole() {
        Role role = new Role();
        role.setName("USER");

        var savedRole = roleRepository.save(role);

        Assertions.assertThat(savedRole).isNotNull().isEqualTo(role);
        Assertions.assertThat(savedRole.getId()).isGreaterThan(0);
    }
}
