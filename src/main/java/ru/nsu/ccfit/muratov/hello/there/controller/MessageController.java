package ru.nsu.ccfit.muratov.hello.there.controller;

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
import ru.nsu.ccfit.muratov.hello.there.dto.message.MessageRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.message.MessageUpdateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.entity.Message;
import ru.nsu.ccfit.muratov.hello.there.repository.MessageRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.UserRepository;
import ru.nsu.ccfit.muratov.hello.there.service.UserEntityService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages between users")
public class MessageController {
    @Autowired
    private UserEntityService userEntityService;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    @Value("${data.message.edit.expiration}")
    private long editExpiration;
    @Value("${data.message.page.size}")
    private int pageSize;

    @Operation(
            summary = "Get messages with one user",
            description = "Gets messages with a user by its ID. Messages are ordered by send time descending and paginated by 10 messages."
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Success",
                    responseCode = "200"
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
                    description = "User not found",
                    responseCode = "404",
                    content = @Content
            )
    })
    @GetMapping(value = "/{userId}", produces = "application/json")
    public List<MessageDto> getPrivateMessages(@PathVariable int userId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity authUser = userEntityService.getUserByUserDetails(userDetails);
        if(!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        UserEntity anotherUser = userRepository.getReferenceById(userId);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("sendTime").descending());
        return messageRepository.getCorrespondenceByUsers(authUser, anotherUser, pageable).stream()
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
    @PostMapping(produces = "application/json", consumes = "application/json")
    @ResponseStatus(code = HttpStatus.CREATED)
    public MessageDto sendMessage(@RequestBody MessageRequestDto dto,
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
        message.setReceiver(receiver);
        message = messageRepository.save(message);
        return new MessageDto(message);
    }


    @Operation(
            summary = "Edit message content",
            description = "Edits the message content by its ID. " +
                    "Message can be edited within 24 hours before it was sent. " +
                    "In case of success edited message will gain last edit time. " +
                    "A user can edit only their own messages."
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Success",
                    responseCode = "200"
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
                    description = "Cannot edit a message",
                    responseCode = "403",
                    content = @Content
            ),
            @ApiResponse(
                    description = "Message not found",
                    responseCode = "404",
                    content = @Content
            )
    })
    @PatchMapping(value = "/{messageId}", produces = "application/json", consumes = "application/json")
    public MessageDto editMessage(@RequestBody MessageUpdateRequestDto dto,
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
        if(userEntityService.isSomeoneBlacklistedEachOther(requester, message.getReceiver())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Edit message with blocker or blocked not allowed");
        }
        Date editDate = new Date();
        if(editDate.getTime() - message.getSendTime().getTime() > editExpiration) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Edit timeout exceeded");
        }

        message.setContent(dto.getNewContent());
        message.setLastEditTime(editDate);
        return new MessageDto(messageRepository.save(message));
    }

    @Operation(
            summary = "Delete message",
            description = "Deletes the message by its ID. " +
                    "Message can be deleted within 24 hours after sending. " +
                    "A user can delete only their own messages."
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Success",
                    responseCode = "204"
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
                    description = "Cannot delete a message",
                    responseCode = "403",
                    content = @Content
            ),
            @ApiResponse(
                    description = "Message not found or already deleted",
                    responseCode = "404",
                    content = @Content
            )
    })
    @DeleteMapping(value = "/{messageId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteMapping(@PathVariable int messageId,
                              @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity requester = userEntityService.getUserByUserDetails(userDetails);
        if(!messageRepository.existsById(messageId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }
        Message message = messageRepository.getReferenceById(messageId);
        if(!requester.equals(message.getSender())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete other's message");
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
}
