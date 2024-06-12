package ru.nsu.ccfit.muratov.hello.there.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.nsu.ccfit.muratov.hello.there.dto.blacklist.group.GroupBlacklistRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.group.GroupCreateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.group.GroupUpdateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.*;
import ru.nsu.ccfit.muratov.hello.there.entity.id.GroupBlacklistId;
import ru.nsu.ccfit.muratov.hello.there.entity.id.SubscriptionId;
import ru.nsu.ccfit.muratov.hello.there.exception.*;
import ru.nsu.ccfit.muratov.hello.there.repository.GroupBlacklistRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.GroupRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.SubscriptionRepository;
import ru.nsu.ccfit.muratov.hello.there.service.GroupService;
import ru.nsu.ccfit.muratov.hello.there.service.UserEntityService;

import java.util.Date;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupBlacklistRepository groupBlacklistRepository;
    @Autowired
    private UserEntityService userService;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Override
    public Group getById(Integer id) throws GroupNotFoundException {
        return groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));
    }

    @Override
    public Page<Group> getGroupList(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
        return groupRepository.findAll(pageable);
    }

    @Override
    public Group create(GroupCreateRequestDto dto, UserEntity owner) {
        Group group = new Group();
        group.setOwner(owner);
        group.setCreateTime(new Date());
        group.setName(dto.getName());
        group.setDescription(dto.getDescription());
        groupRepository.save(group);
        subscribeInternal(group, owner);
        return group;
    }

    @Override
    public Group update(Integer groupId, GroupUpdateRequestDto newData, UserEntity requester)
            throws GroupAdminAccessDeniedException, BadRequestException, GroupNotFoundException {
        Group group = getById(groupId);
        if(!checkOwner(group, requester)) {
            throw new GroupAdminAccessDeniedException("User is not an owner of the group");
        }
        if(group.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.GONE, "Group was deleted");
        }
        boolean isEmpty = true;
        String newName = newData.getName();
        String newDescription = newData.getDescription();
        if(newName != null) {
            isEmpty = false;
            group.setName(newName);
        }
        if(newDescription != null) {
            isEmpty = false;
            group.setDescription(newDescription);
        }
        if(isEmpty) {
            throw new BadRequestException("Empty update request received");
        }
        return groupRepository.save(group);
    }

    @Override
    public void delete(Integer groupId, UserEntity requester) throws GroupAdminAccessDeniedException, GroupNotFoundException {
        Group group = getById(groupId);
        if(!checkOwner(group, requester)) {
            throw new GroupAdminAccessDeniedException("User is not an owner of the group");
        }
        group.setDeleted(true);
        groupRepository.save(group);
    }



    @Override
    public Subscription subscribe(Integer groupId, UserEntity user) throws GroupBlacklistedException, GroupNotFoundException {
        Group group = getById(groupId);
        if(isBlacklisted(group, user)) {
            throw new GroupBlacklistedException("Cannot subscribe to group blocked the user");
        }
        return subscribeInternal(group, user);
    }

    @Override
    public void unsubscribe(Integer groupId, UserEntity user) throws BadRequestException, GroupNotFoundException {
        Group group = getById(groupId);
        if(user.equals(group.getOwner())) {
            throw new BadRequestException("Group owner cannot unsubscribe from their group");
        }
        unsubscribeInternal(group, user);
    }

    @Override
    public List<Subscription> getSubscriberList(Integer groupId, Pageable pageable, UserEntity requester)
            throws GroupBlacklistedException, GroupNotFoundException {
        Group group = getById(groupId);
        if(isBlacklisted(group, requester)) {
            throw new GroupBlacklistedException("Cannot subscribe to group blocked the user");
        }
        return subscriptionRepository.findByGroup(group, pageable);
    }

    @Override
    public GroupBlacklist addToBlacklist(Integer groupId, GroupBlacklistRequestDto dto, UserEntity requester)
            throws GroupAdminAccessDeniedException, BadRequestException, ResourceNotFoundException {
        Group group = getById(groupId);
        if(!checkOwner(group, requester)) {
            throw new GroupAdminAccessDeniedException("Cannot access blacklist not being the owner");
        }
        UserEntity blocked = userService.getById(dto.getUserId());
        if(requester.equals(blocked)) {
            throw new BadRequestException("Attempt to add themselves to blacklist");
        }
        unsubscribeInternal(group, blocked);
        GroupBlacklistId groupBlacklistId = new GroupBlacklistId(group.getId(), blocked.getId());
        GroupBlacklist groupBlacklist = new GroupBlacklist();
        groupBlacklist.setId(groupBlacklistId);
        groupBlacklist.setGroup(group);
        groupBlacklist.setBlockedUser(blocked);
        groupBlacklist.setBlockTime(new Date());
        groupBlacklist.setReason(dto.getReason());
        return groupBlacklistRepository.save(groupBlacklist);
    }

    @Override
    public void removeFromBlacklist(Integer groupId, Integer blockedId, UserEntity requester) throws GroupAdminAccessDeniedException, ResourceNotFoundException {
        Group group = getById(groupId);
        UserEntity blocked = userService.getById(blockedId);
        if(!checkOwner(group, requester)) {
            throw new GroupAdminAccessDeniedException("Cannot access blacklist not being the owner");
        }
        groupBlacklistRepository.deleteById(new GroupBlacklistId(group.getId(), blocked.getId()));
    }

    @Override
    public Page<GroupBlacklist> getBlacklist(Integer groupId, int pageNumber, int pageSize, UserEntity requester)
            throws GroupAdminAccessDeniedException, ResourceNotFoundException {
        Group group = getById(groupId);
        if(!checkOwner(group, requester)) {
            throw new GroupAdminAccessDeniedException("Cannot access blacklist not being the owner");
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
        return groupBlacklistRepository.findByGroup(group, pageable);
    }

    @Override
    public boolean isBlacklisted(Group group, UserEntity user) {
        return groupBlacklistRepository.existsById(new GroupBlacklistId(group.getId(), user.getId()));
    }

    @Override
    public boolean checkOwner(Group group, UserEntity requester) {
        return requester.equals(group.getOwner());
    }



    private Subscription subscribeInternal(Group group, UserEntity user) {
        SubscriptionId subscriptionId = new SubscriptionId(group.getId(), user.getId());
        Subscription subscription = new Subscription();
        subscription.setGroup(group);
        subscription.setSubscriber(user);
        subscription.setSubscriptionTime(new Date());
        subscription.setId(subscriptionId);
        return subscriptionRepository.save(subscription);
    }

    private void unsubscribeInternal(Group group, UserEntity user) {
        subscriptionRepository.deleteById(new SubscriptionId(group.getId(), user.getId()));
    }
}
