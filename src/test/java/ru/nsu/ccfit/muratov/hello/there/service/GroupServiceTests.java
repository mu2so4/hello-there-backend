package ru.nsu.ccfit.muratov.hello.there.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nsu.ccfit.muratov.hello.there.dto.group.GroupCreateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Group;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.entity.id.GroupBlacklistId;
import ru.nsu.ccfit.muratov.hello.there.repository.GroupBlacklistRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.GroupRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.SubscriptionRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.UserRepository;
import ru.nsu.ccfit.muratov.hello.there.service.impl.GroupServiceImpl;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTests {
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private GroupBlacklistRepository groupBlacklistRepository;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GroupServiceImpl groupService;

    @Test
    @DisplayName("Create group")
    public void create() {
        UserEntity owner = createTestUser();
        Integer userId = owner.getId();
        Integer groupId = 100;
        owner.setId(userId);
        String name = "Test group";
        String description = "To mock or no to mock that is the question";
        GroupCreateRequestDto dto = new GroupCreateRequestDto();
        dto.setName(name);
        dto.setDescription(description);
        when(userRepository.getReferenceById(userId)).thenReturn(owner);
        when(groupRepository.save(any(Group.class))).then((invocation) -> {
            Group savedGroup = invocation.getArgument(0, Group.class);
            savedGroup.setId(groupId);
            return savedGroup;
        });
        when(groupBlacklistRepository.existsById(any(GroupBlacklistId.class))).thenReturn(false);

        Group savedGroup = groupService.create(dto, userRepository.getReferenceById(userId));

        Assertions.assertThat(savedGroup)
                .isNotNull();
        Assertions.assertThat(savedGroup.getName())
                .isEqualTo(name);
        Assertions.assertThat(savedGroup.getDescription())
                .isEqualTo(description);
        Assertions.assertThat(savedGroup.getId())
                .isEqualTo(groupId);
        Assertions.assertThat(savedGroup.getOwner())
                .isEqualTo(owner);
    }

    private Group createTestGroup(UserEntity owner) {
        Group group = new Group();
        group.setId(1);
        group.setName("Dummy group");
        group.setDescription("Group for testing repository");
        group.setCreateTime(new Date());
        group.setOwner(owner);
        return group;
    }

    private UserEntity createTestUser() {
        UserEntity user = new UserEntity();
        user.setId(1);
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
