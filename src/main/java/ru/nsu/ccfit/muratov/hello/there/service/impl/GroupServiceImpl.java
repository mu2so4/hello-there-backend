package ru.nsu.ccfit.muratov.hello.there.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.muratov.hello.there.entity.*;
import ru.nsu.ccfit.muratov.hello.there.entity.id.SubscriptionId;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupBlacklistedException;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.repository.GroupBlacklistRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.GroupRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.SubscriptionRepository;
import ru.nsu.ccfit.muratov.hello.there.service.GroupService;

import java.util.Date;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupBlacklistRepository groupBlacklistRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Override
    public Group getById(Integer id) throws GroupNotFoundException {
        if(!groupRepository.existsById(id)) {
            throw new GroupNotFoundException("group not found");
        }
        return groupRepository.getReferenceById(id);
    }

    @Override
    public boolean isBlacklisted(Group group, UserEntity user) {
        return groupBlacklistRepository.existsById(new GroupBlacklistId(group.getId(), user.getId()));
    }

    @Override
    public Subscription subscribe(Group group, UserEntity user) throws GroupBlacklistedException {
        if(isBlacklisted(group, user)) {
            throw new GroupBlacklistedException("Cannot subscribe to group blocked the user");
        }
        SubscriptionId subscriptionId = new SubscriptionId(group.getId(), user.getId());
        Subscription subscription = new Subscription();
        subscription.setGroup(group);
        subscription.setSubscriber(user);
        subscription.setSubscriptionTime(new Date());
        subscription.setId(subscriptionId);
        return subscriptionRepository.save(subscription);
    }

    @Override
    public List<Subscription> getSubscriberList(Group group, UserEntity requester, Pageable pageable)
            throws GroupBlacklistedException {
        if(isBlacklisted(group, requester)) {
            throw new GroupBlacklistedException("Cannot subscribe to group blocked the user");
        }
        return subscriptionRepository.findByGroup(group, pageable);
    }
}
