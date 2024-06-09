package ru.nsu.ccfit.muratov.hello.there.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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
    public Page<Message> getCorrespondence(UserEntity user1, UserEntity user2, Pageable pageable) {
        return messageRepository.getCorrespondenceByUsers(user1, user2, pageable);
    }

    @Override
    public Message getById(Integer id) throws MessageNotFoundException {
        if(!messageRepository.existsById(id)) {
            throw new MessageNotFoundException("Message not found");
        }
        return messageRepository.getReferenceById(id);
    }

    @Override
    public Message sendMessage(UserEntity sender, UserEntity receiver, String content, Integer repliedMessageId)
            throws UserBlacklistException, MessageNotFoundException {
        if(userEntityService.isSomeoneBlacklistedEachOther(sender, receiver)) {
            throw new UserBlacklistException("Cannot send message to blocker or blocked");
        }

        Message message = new Message();
        message.setRepliedMessage(getById(repliedMessageId));
        message.setContent(content);
        message.setSendTime(new Date());
        message.setSender(sender);
        message.setReceiver(receiver);
        return messageRepository.save(message);
    }

    @Override
    public Message edit(Message message, UserEntity requester, String newContent) throws AccessDeniedException {
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

        message.setContent(newContent);
        message.setLastEditTime(editDate);
        return messageRepository.save(message);
    }

    @Override
    public void delete(Message message, UserEntity requester) throws AccessDeniedException {
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
