package ru.nsu.ccfit.muratov.hello.there.controller.message;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.nsu.ccfit.muratov.hello.there.dto.message.MessageDto;
import ru.nsu.ccfit.muratov.hello.there.dto.message.PrivateMessageRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.message.PrivateMessageResponseDto;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.entity.message.Message;
import ru.nsu.ccfit.muratov.hello.there.entity.message.PrivateMessage;
import ru.nsu.ccfit.muratov.hello.there.repository.UserRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.message.MessageRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.message.PrivateMessageRepository;
import ru.nsu.ccfit.muratov.hello.there.service.UserEntityService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/messages/users")
@Tag(name = "Private messages")
public class PrivateMessageController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private PrivateMessageRepository privateMessageRepository;
    @Autowired
    private UserEntityService userEntityService;

    @Value("${data.message.page.size}")
    private int pageSize;

    @GetMapping("/{userId}")
    public List<MessageDto> getPrivateMessages(@PathVariable int userId, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("message.sendTime").descending());
        return privateMessageRepository.findAll(pageable).stream()
                .map(MessageDto::new)
                .toList();
    }

    @Operation(
            summary = "Send a private message to another user",
            description = "Send a private message to another user. " +
                    "A sender sets message content and replied message if necessary. " +
                    "A message cannot be sent if the received blacklisted the sender and/or vice versa."
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Success",
                    responseCode = "201"
            ),
            @ApiResponse(
                    description = "Bad request",
                    responseCode = "400",
                    content = @Content
            ),
            @ApiResponse(
                    description = "Unauthorized",
                    responseCode = "401",
                    content = @Content
            ),
            @ApiResponse(
                    description = "Cannot send a message",
                    responseCode = "403",
                    content = @Content
            ),
            @ApiResponse(
                    description = "A receiver or a replied message not found",
                    responseCode = "404",
                    content = @Content
            )
    })
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public PrivateMessageResponseDto sendMessage(@RequestBody PrivateMessageRequestDto dto,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity sender = userEntityService.getUserByUserDetails(userDetails);
        int receiverId = dto.getReceiverId();

        if(!userRepository.existsById(receiverId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver not found");
        }
        UserEntity receiver = userRepository.getReferenceById(receiverId);
        if(userEntityService.isSomeoneBlacklistedEachOther(sender, receiver)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot send message to blocker or blocked");
        }

        Message message = new Message();
        Integer id = dto.getRepliedMessageId();
        if(id != null) {
            if(!messageRepository.existsById(id)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Replied message not found");
            }
            message.setRepliedMessage(messageRepository.getReferenceById(id));
        }
        message.setContent(dto.getContent());
        message.setSendTime(new Date());
        message.setSender(sender);
        message = messageRepository.save(message);

        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.setMessage(message);
        privateMessage.setReceiver(receiver);
        privateMessage = privateMessageRepository.save(privateMessage);
        return new PrivateMessageResponseDto(privateMessage);
    }
}
