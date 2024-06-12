package ru.nsu.ccfit.muratov.hello.there.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.nsu.ccfit.muratov.hello.there.dto.blacklist.group.GroupBlacklistRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.group.GroupCreateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.group.GroupUpdateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Group;
import ru.nsu.ccfit.muratov.hello.there.entity.GroupBlacklist;
import ru.nsu.ccfit.muratov.hello.there.entity.Subscription;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.*;

import java.util.List;

public interface GroupService {
    Group getById(Integer id) throws GroupNotFoundException;

    Page<Group> getGroupList(int pageNumber, int pageSize);

    Group create(GroupCreateRequestDto dto, UserEntity requester);
    Group update(Integer groupId, GroupUpdateRequestDto newData, UserEntity requester) throws GroupAdminAccessDeniedException, BadRequestException, GroupNotFoundException;
    void delete(Integer groupId, UserEntity requester) throws GroupAdminAccessDeniedException, GroupNotFoundException;

    Subscription subscribe(Integer groupId, UserEntity user) throws GroupBlacklistedException, GroupNotFoundException;
    void unsubscribe(Integer groupId, UserEntity user) throws BadRequestException, GroupNotFoundException;
    List<Subscription> getSubscriberList(Integer groupId, Pageable pageable, UserEntity requester) throws GroupBlacklistedException, GroupNotFoundException;

    GroupBlacklist addToBlacklist(Integer groupId, GroupBlacklistRequestDto dto, UserEntity requester) throws GroupAdminAccessDeniedException, BadRequestException, ResourceNotFoundException;
    void removeFromBlacklist(Integer groupId, Integer blockedId, UserEntity requester) throws GroupAdminAccessDeniedException, ResourceNotFoundException;
    Page<GroupBlacklist> getBlacklist(Integer groupId, int pageNumber, int pageSize, UserEntity requester) throws GroupAdminAccessDeniedException, ResourceNotFoundException;
    boolean isBlacklisted(Group group, UserEntity user);

    boolean checkOwner(Group group, UserEntity requester);
}
