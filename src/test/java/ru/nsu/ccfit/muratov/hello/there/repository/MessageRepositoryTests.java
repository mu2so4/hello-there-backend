package ru.nsu.ccfit.muratov.hello.there.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.nsu.ccfit.muratov.hello.there.entity.Message;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class MessageRepositoryTests {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

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

    private static Message createTestMessage(UserEntity sender, UserEntity receiver, String content) {
        Message message = new Message();
        message.setContent(content);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setSendTime(new Date());
        return message;
    }

    @Test
    @DisplayName("Create message")
    public void create() throws ParseException {
        UserEntity sender = createTestUser("mu2so4");
        UserEntity receiver = createTestUser("muso4");
        userRepository.saveAll(List.of(sender, receiver));
        Message message = createTestMessage(sender, receiver, "Hello there!");

        var savedMessage = messageRepository.save(message);

        Assertions.assertThat(savedMessage)
                .isNotNull()
                .isEqualTo(message);
        Assertions.assertThat(savedMessage.getId())
                .isGreaterThan(0);
    }

    @Test
    @DisplayName("Get message by id")
    public void getById() throws ParseException {
        UserEntity sender = createTestUser("mu2so4");
        UserEntity receiver = createTestUser("muso4");
        userRepository.saveAll(List.of(sender, receiver));
        Message message = createTestMessage(sender, receiver, "Hello there!");
        messageRepository.save(message);

        var savedMessage = messageRepository.getReferenceById(message.getId());

        Assertions.assertThat(savedMessage)
                .isNotNull()
                .isEqualTo(message);
    }

    @Test
    @DisplayName("Update message")
    public void update() throws ParseException {
        UserEntity sender = createTestUser("mu2so4");
        UserEntity receiver = createTestUser("muso4");
        userRepository.saveAll(List.of(sender, receiver));
        Message message = createTestMessage(sender, receiver, "Hello there!");
        messageRepository.save(message);
        Integer id = message.getId();

        var savedMessage = messageRepository.getReferenceById(message.getId());
        savedMessage.setContent("General Kenobi!");
        savedMessage.setLastEditTime(new Date());
        var updatedMessage = messageRepository.save(savedMessage);

        Assertions.assertThat(updatedMessage)
                .isNotNull()
                .isEqualTo(message);
        Assertions.assertThat(updatedMessage.getId())
                .isEqualTo(id);
    }

    @Test
    @DisplayName("Delete message")
    public void delete() throws ParseException {
        UserEntity sender = createTestUser("mu2so4");
        UserEntity receiver = createTestUser("muso4");
        userRepository.saveAll(List.of(sender, receiver));
        Message message = createTestMessage(sender, receiver, "Hello there!");
        messageRepository.save(message);
        Integer id = message.getId();

        messageRepository.delete(message);

        Assertions.assertThat(messageRepository.existsById(id))
                .isFalse();
    }

    @Test
    @DisplayName("Get correspondence")
    public void getCorrespondence() throws ParseException, InterruptedException {
        final long DELAY = 5;
        UserEntity user1 = createTestUser("Obi_Wan");
        UserEntity user2 = createTestUser("Grievous");
        userRepository.saveAll(List.of(user1, user2));
        List<Message> messages = new ArrayList<>();
        messages.add(createTestMessage(user1, user2, "Hello there!"));
        Thread.sleep(DELAY);
        messages.add(createTestMessage(user2, user1, "General Kenobi!"));
        Thread.sleep(DELAY);
        messages.add(createTestMessage(user1, user2, "Your move"));
        Thread.sleep(DELAY);
        messages.add(createTestMessage(user2, user1,
                "You fool! I've been trained in your Jedi arts by Count Dooku!"));
        Thread.sleep(DELAY);
        messages.add(createTestMessage(user2, user1, "Attack, Kenobi!"));
        messages.get(1).setRepliedMessage(messages.get(0));
        messages.get(4).setRepliedMessage(messages.get(3));
        messageRepository.saveAll(messages);
        var messagesReversed = new ArrayList<>(messages);
        Collections.reverse(messagesReversed);

        var savedMessages = messageRepository.getCorrespondenceByUsers(user1, user2,
                PageRequest.of(0, 10, Sort.by("sendTime").descending()));

        Assertions.assertThat(savedMessages)
                .isNotEmpty()
                .containsExactlyElementsOf(messagesReversed);
    }
}
