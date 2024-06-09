package ru.nsu.ccfit.muratov.hello.there.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.nsu.ccfit.muratov.hello.there.dto.group.GroupUpdateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Group;
import ru.nsu.ccfit.muratov.hello.there.entity.GroupBlacklist;
import ru.nsu.ccfit.muratov.hello.there.entity.Subscription;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.BadRequestException;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupAdminAccessDeniedException;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupBlacklistedException;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupNotFoundException;

import java.util.List;

public interface GroupService {
    Group getById(Integer id) throws GroupNotFoundException;
    Page<Group> getGroupList(Pageable pageable);

    Subscription subscribe(Group group, UserEntity user) throws GroupBlacklistedException;
    void unsubscribe(Group group, UserEntity user) throws BadRequestException;
    List<Subscription> getSubscriberList(Group group, UserEntity requester, Pageable pageable) throws GroupBlacklistedException;

    Group create(UserEntity owner, String name, String description);
    Group update(Group group, UserEntity requester, GroupUpdateRequestDto newData) throws GroupAdminAccessDeniedException, BadRequestException;
    void delete(Group group, UserEntity requester) throws GroupAdminAccessDeniedException;

    GroupBlacklist addToBlacklist(Group group, UserEntity blocked, String reason, UserEntity requester) throws GroupAdminAccessDeniedException, BadRequestException;
    void removeFromBlacklist(Group group, UserEntity blocked, UserEntity requester) throws GroupAdminAccessDeniedException;
    Page<GroupBlacklist> getBlacklist(Group group, UserEntity requester, Pageable pageable) throws GroupAdminAccessDeniedException;
    boolean isBlacklisted(Group group, UserEntity user);

    boolean checkOwner(Group group, UserEntity requester);
}
