package ru.nsu.ccfit.muratov.hello.there.service;

import ru.nsu.ccfit.muratov.hello.there.entity.Group;
import ru.nsu.ccfit.muratov.hello.there.entity.Subscription;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupBlacklistedException;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupNotFoundException;

public interface GroupService {
    Group getById(Integer id) throws GroupNotFoundException;

    boolean isBlacklisted(Group group, UserEntity user);

    Subscription subscribe(Group group, UserEntity user) throws GroupBlacklistedException;
}
