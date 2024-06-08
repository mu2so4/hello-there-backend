package ru.nsu.ccfit.muratov.hello.there.service;

import org.springframework.data.domain.Pageable;
import ru.nsu.ccfit.muratov.hello.there.entity.Group;
import ru.nsu.ccfit.muratov.hello.there.entity.Subscription;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.BadRequestException;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupBlacklistedException;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupNotFoundException;

import java.util.List;

public interface GroupService {
    Group getById(Integer id) throws GroupNotFoundException;

    Subscription subscribe(Group group, UserEntity user) throws GroupBlacklistedException;

    void unsubscribe(Group group, UserEntity user) throws BadRequestException;

    List<Subscription> getSubscriberList(Group group, UserEntity requester, Pageable pageable) throws GroupBlacklistedException;
}
