package ru.nsu.ccfit.muratov.hello.there.controller.message;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.nsu.ccfit.muratov.hello.there.dto.message.MessageUpdateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.message.MessageUpdateResponseDto;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.entity.message.Message;
import ru.nsu.ccfit.muratov.hello.there.repository.message.MessageRepository;
import ru.nsu.ccfit.muratov.hello.there.service.UserEntityService;

import java.util.Date;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Common message actions")
public class MessageController {
    @Autowired
    private UserEntityService userEntityService;
    @Autowired
    private MessageRepository messageRepository;
    @Value("${data.message.edit.expiration}")
    private long editExpiration;

    @PatchMapping("/{messageId}")
    public MessageUpdateResponseDto editMessage(@RequestBody MessageUpdateRequestDto dto,
                                                @PathVariable int messageId,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity requester = userEntityService.getUserByUserDetails(userDetails);
        if(!messageRepository.existsById(messageId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }
        Message message = messageRepository.getReferenceById(messageId);
        if(!requester.equals(message.getSender())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot edit other's message");
        }
        Date editDate = new Date();
        if(editDate.getTime() - message.getSendTime().getTime() > editExpiration) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Edit timeout exceeded");
        }

        message.setContent(dto.getNewContent());
        message.setLastEditTime(editDate);
        return new MessageUpdateResponseDto(messageRepository.save(message));
    }

    @DeleteMapping("/{messageId}")
    public void deleteMapping() {

    }
}
