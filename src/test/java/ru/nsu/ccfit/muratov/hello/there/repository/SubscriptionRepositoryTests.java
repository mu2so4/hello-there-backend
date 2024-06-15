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
import ru.nsu.ccfit.muratov.hello.there.entity.Subscription;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.entity.id.SubscriptionId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class SubscriptionRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private SubscriptionRepository subscript;

    private Group group;
    private Subscription subscription;

    @BeforeEach
    public void init() throws ParseException {
        UserEntity owner = createTestUser("mu2so4");
        group = createTestGroup(owner);
        UserEntity subscriber = createTestUser("muso4");
        userRepository.saveAll(List.of(owner, subscriber));
        groupRepository.save(group);
        subscription = createTestSubscription(group, subscriber);
    }

    @Test
    @DisplayName("Create new group subscription")
    public void create() {


        var saved = subscript.save(subscription);

        Assertions.assertThat(saved.getSubscriptionTime())
                .hasSameTimeAs(subscription.getSubscriptionTime());
        Assertions.assertThat(saved)
                .isNotNull()
                .isEqualTo(subscription);
    }

    @Test
    @DisplayName("Get subscription by group and user")
    public void getById() {
        subscript.save(subscription);

        var saved = subscript.getReferenceById(subscription.getId());

        Assertions.assertThat(saved)
                .isNotNull()
                .isEqualTo(subscription);
    }

    @Test
    @DisplayName("Revoke group subscription")
    public void delete() {
        subscript.save(subscription);

        subscript.deleteById(subscription.getId());

        Assertions.assertThat(subscript.existsById(subscription.getId()))
                .isFalse();
    }

    @Test
    @DisplayName("Get group's subscriptions")
    public void getBlacklist() throws ParseException, InterruptedException {
        final long DELAY = 2;
        Thread.sleep(DELAY);
        UserEntity subscriber2 = createTestUser("mu3po4");
        Thread.sleep(DELAY);
        UserEntity subscriber3 = createTestUser("ch3coomu");
        userRepository.saveAll(List.of(subscriber2, subscriber3));
        Subscription subscription2 = createTestSubscription(group, subscriber2);
        Subscription subscription3 = createTestSubscription(group, subscriber3);
        List<Subscription> blacklist = List.of(subscription, subscription2, subscription3);
        subscript.saveAll(blacklist);

        var savedBlacklist = subscript.findByGroup(group,
                PageRequest.of(0, 10, Sort.by("subscriptionTime")));

        Assertions.assertThat(savedBlacklist)
                .isNotEmpty()
                .containsExactlyElementsOf(blacklist);
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

    private static Subscription createTestSubscription(Group group, UserEntity subscriber) {
        Subscription subscription = new Subscription();
        subscription.setGroup(group);
        subscription.setSubscriber(subscriber);
        subscription.setId(new SubscriptionId(group.getId(), subscriber.getId()));
        subscription.setSubscriptionTime(new Date());
        return subscription;
    }
}
