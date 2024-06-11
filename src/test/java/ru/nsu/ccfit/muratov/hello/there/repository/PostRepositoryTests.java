package ru.nsu.ccfit.muratov.hello.there.repository;

import jakarta.persistence.EntityNotFoundException;
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
import ru.nsu.ccfit.muratov.hello.there.entity.Post;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class PostRepositoryTests {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;

    private Group group;
    private Post post;

    @BeforeEach
    public void init() {
        UserEntity owner = createTestUser();
        group = createTestGroup(owner);
        post = createTestPost(group);
        userRepository.save(owner);
        groupRepository.save(group);
    }

    @Test
    @DisplayName("Create post")
    public void create() {


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
        postRepository.save(post);

        var found = postRepository.getReferenceById(post.getId());

        Assertions.assertThat(found)
                .isNotNull()
                .isEqualTo(post);
    }

    @Test
    @DisplayName("Update post")
    public void update() throws InterruptedException {
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
    @DisplayName("Delete post")
    public void delete() {
        postRepository.save(post);
        Integer id = post.getId();

        postRepository.delete(post);

        Assertions.assertThatThrownBy(() -> postRepository.getReferenceById(id))
                .hasCauseInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Get posts by host group")
    public void getByGroup() throws InterruptedException {
        Post post1 = createTestPost(group);
        Thread.sleep(2);
        Post post2 = createTestPost(group);
        post2.setContent("Bye!");
        List<Post> posts = new ArrayList<>();
        posts.add(post1);
        posts.add(post2);
        postRepository.saveAll(posts);
        List<Post> reversedPosts = new ArrayList<>(posts);
        Collections.reverse(reversedPosts);

        var savedPosts = postRepository.findByGroup(group,
                PageRequest.of(0, 10, Sort.by("createTime").descending()));

        Assertions.assertThat(savedPosts)
                .isNotNull()
                .containsExactlyElementsOf(reversedPosts);
    }

    private static Post createTestPost(Group hostGroup) {
        Post post = new Post();
        post.setGroup(hostGroup);
        post.setCreateTime(new Date());
        post.setContent("This is test post");
        return post;
    }

    private static Group createTestGroup(UserEntity owner) {
        Group group = new Group();
        group.setName("Dummy group");
        group.setDescription("Group for testing repository");
        group.setCreateTime(new Date());
        group.setOwner(owner);
        return group;
    }

    private static UserEntity createTestUser() {
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
