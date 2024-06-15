package ru.nsu.ccfit.muratov.hello.there.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.nsu.ccfit.muratov.hello.there.dto.message.MessageRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.message.MessageUpdateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Message;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.*;
import ru.nsu.ccfit.muratov.hello.there.repository.MessageRepository;
import ru.nsu.ccfit.muratov.hello.there.service.MessageService;
import ru.nsu.ccfit.muratov.hello.there.service.UserEntityService;

import java.util.Date;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserEntityService userEntityService;

    @Value("${data.message.edit.expiration}")
    private long editExpiration;

    @Override
    public Page<Message> getCorrespondence(UserEntity authUser, Integer anotherUserId, int pageNumber, int pageSize) throws UserNotFoundException {
        UserEntity anotherUser = userEntityService.getById(anotherUserId);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("sendTime").descending());
        return messageRepository.getCorrespondenceByUsers(authUser, anotherUser, pageable);
    }

    @Override
    public Message getById(Integer id) throws MessageNotFoundException {
        return messageRepository.findById(id).orElseThrow(() ->
            new MessageNotFoundException("Message not found"));
    }

    @Override
    public Message sendMessage(UserEntity sender, MessageRequestDto dto)
            throws UserBlacklistException, MessageNotFoundException, UserNotFoundException {
        UserEntity receiver = userEntityService.getById(dto.getReceiverId());
        if(userEntityService.isSomeoneBlacklistedEachOther(sender, receiver)) {
            throw new UserBlacklistException("Cannot send message to blocker or blocked");
        }

        Message message = new Message();
        if(dto.getRepliedMessageId() != null) {
            message.setRepliedMessage(getById(dto.getRepliedMessageId()));
        }
        message.setContent(dto.getContent());
        message.setSendTime(new Date());
        message.setSender(sender);
        message.setReceiver(receiver);
        return messageRepository.save(message);
    }

    @Override
    public Message edit(Integer messageId, MessageUpdateRequestDto dto, UserEntity requester)
            throws AccessDeniedException, MessageNotFoundException {
        Message message = getById(messageId);
        if(!isMessageSender(message, requester)) {
            throw new MessageAccessFailedException("Cannot edit other's message");
        }
        if(userEntityService.isSomeoneBlacklistedEachOther(requester, message.getReceiver())) {
            throw new UserBlacklistException("Edit message with blocker or blocked not allowed");
        }
        Date editDate = new Date();
        if(editDate.getTime() - message.getSendTime().getTime() > editExpiration) {
            throw new MessageTimeoutExceededException("Edit timeout exceeded");
        }

        message.setContent(dto.getNewContent());
        message.setLastEditTime(editDate);
        return messageRepository.save(message);
    }

    @Override
    public void delete(Integer messageId, UserEntity requester) throws AccessDeniedException, MessageNotFoundException {
        Message message = getById(messageId);
        if(!isMessageSender(message, requester)) {
            throw new MessageAccessFailedException("Cannot delete other's message");
        }
        if(userEntityService.isSomeoneBlacklistedEachOther(requester, message.getReceiver())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Delete message with blocker or blocked not allowed");
        }
        Date deleteTime = new Date();
        if(deleteTime.getTime() - message.getSendTime().getTime() > editExpiration) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Delete timeout exceeded");
        }
        messageRepository.delete(message);
    }

    private boolean isMessageSender(Message message, UserEntity user) {
        return user.equals(message.getSender());
    }
}
