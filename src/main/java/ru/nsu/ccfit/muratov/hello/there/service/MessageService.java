package ru.nsu.ccfit.muratov.hello.there.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.nsu.ccfit.muratov.hello.there.entity.Message;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.AccessDeniedException;
import ru.nsu.ccfit.muratov.hello.there.exception.MessageNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.exception.UserBlacklistException;

public interface MessageService {
    Page<Message> getCorrespondence(UserEntity user1, UserEntity user2, Pageable pageable);

    Message getById(Integer id) throws MessageNotFoundException;

    Message sendMessage(UserEntity sender, UserEntity receiver, String content, Integer repliedMessageId)
            throws UserBlacklistException, MessageNotFoundException;

    Message edit(Message message, UserEntity requester, String newContent)
        throws AccessDeniedException;

    void delete(Message message, UserEntity requester)
        throws AccessDeniedException;
}
