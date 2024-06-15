package ru.nsu.ccfit.muratov.hello.there.service;

import org.springframework.data.domain.Page;
import ru.nsu.ccfit.muratov.hello.there.dto.message.MessageRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.message.MessageUpdateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Message;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.AccessDeniedException;
import ru.nsu.ccfit.muratov.hello.there.exception.MessageNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.exception.UserBlacklistException;
import ru.nsu.ccfit.muratov.hello.there.exception.UserNotFoundException;

public interface MessageService {
    Page<Message> getCorrespondence(UserEntity authUser, Integer anotherUserId, int pageNumber, int pageSize) throws UserNotFoundException;

    Message getById(Integer id) throws MessageNotFoundException;

    Message sendMessage(UserEntity sender, MessageRequestDto dto)
            throws UserBlacklistException, MessageNotFoundException, UserNotFoundException;

    Message edit(Integer messageId, MessageUpdateRequestDto dto, UserEntity requester)
            throws AccessDeniedException, MessageNotFoundException;

    void delete(Integer messageId, UserEntity requester)
            throws AccessDeniedException, MessageNotFoundException;
}
