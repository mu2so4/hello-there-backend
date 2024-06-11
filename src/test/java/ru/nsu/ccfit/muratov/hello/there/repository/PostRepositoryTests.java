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
import ru.nsu.ccfit.muratov.hello.there.entity.Post;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;

import java.text.ParseException;
import java.util.Date;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class PostRepositoryTests {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;

    @Test
    @DisplayName("Create post")
    public void create() {
        UserEntity owner = createTestUser();
        Group group = createTestGroup(owner);
        Post post = createTestPost(group);

        Post savedPost = postRepository.save(post);

        Assertions.assertThat(savedPost)
                .isNotNull()
                .isEqualTo(post);
        Assertions.assertThat(savedPost.getId())
                .isGreaterThan(0);
    }

    @Test
    @DisplayName("Get post by ID")
    public void getById() {
        UserEntity owner = createTestUser();
        Group group = createTestGroup(owner);
        Post post = createTestPost(group);
        postRepository.save(post);

        var found = postRepository.getReferenceById(post.getId());

        Assertions.assertThat(found)
                .isNotNull()
                .isEqualTo(post);
    }

    @Test
    @DisplayName("Update")
    public void update() throws InterruptedException {
        UserEntity owner = createTestUser();
        Group group = createTestGroup(owner);
        Post post = createTestPost(group);
        Post savedPost = postRepository.save(post);
        Integer id = post.getId();

        Thread.sleep(2);
        String newContent = "I'm going to NSU";
        Date lastEditTime = new Date();
        savedPost.setContent(newContent);
        savedPost.setLastEditTime(lastEditTime);
        Post updatedPost = postRepository.save(savedPost);

        Assertions.assertThat(updatedPost)
                .isNotNull()
                .isEqualTo(savedPost);
        Assertions.assertThat(updatedPost.getId())
                .isEqualTo(id);
        Assertions.assertThat(updatedPost.getContent())
                .isEqualTo(newContent);
        Assertions.assertThat(updatedPost.getLastEditTime())
                .isEqualTo(lastEditTime);
    }

    @Test
    @DisplayName("Delete group")
    public void delete() throws ParseException {
        UserEntity owner = createTestUser();
        Group group = createTestGroup(owner);
        Post post = createTestPost(group);
        postRepository.save(post);
        Integer id = post.getId();

        postRepository.delete(post);

        Assertions.assertThatThrownBy(() -> postRepository.getReferenceById(id))
                .hasCauseInstanceOf(EntityNotFoundException.class);
    }

    private Post createTestPost(Group hostGroup) {
        Post post = new Post();
        groupRepository.save(hostGroup);
        post.setGroup(hostGroup);
        post.setCreateTime(new Date());
        post.setContent("This is test post");
        return post;
    }

    private Group createTestGroup(UserEntity owner) {
        Group group = new Group();
        group.setName("Dummy group");
        group.setDescription("Group for testing repository");
        group.setCreateTime(new Date());
        userRepository.save(owner);
        group.setOwner(owner);
        return group;
    }

    private UserEntity createTestUser() {
        UserEntity user = new UserEntity();
        user.setUsername("mu2so4");
        user.setPassword("1234");
        user.setFirstName("Maxim");
        user.setLastName("Muratov");
        Date date = new Date();
        user.setBirthday(date);
        user.setRegistrationTime(new Date());
        return user;
    }
}
