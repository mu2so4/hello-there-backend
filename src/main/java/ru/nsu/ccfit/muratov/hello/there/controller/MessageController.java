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
import ru.nsu.ccfit.muratov.hello.there.dto.message.MessageDto;
import ru.nsu.ccfit.muratov.hello.there.dto.message.MessageRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.message.MessageUpdateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.entity.Message;
import ru.nsu.ccfit.muratov.hello.there.exception.*;
import ru.nsu.ccfit.muratov.hello.there.service.MessageService;
import ru.nsu.ccfit.muratov.hello.there.service.UserEntityService;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages between users")
public class MessageController {
    @Autowired
    private UserEntityService userEntityService;
    @Autowired
    private MessageService messageService;

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
                                               @AuthenticationPrincipal UserDetails userDetails) throws UserNotFoundException {
        UserEntity authUser = userEntityService.getUserByUserDetails(userDetails);
        UserEntity anotherUser = userEntityService.getById(userId);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("sendTime").descending());
        return messageService.getCorrespondence(authUser, anotherUser, pageable).stream()
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
                                  @AuthenticationPrincipal UserDetails userDetails) throws ResourceNotFoundException, UserBlacklistException {
        UserEntity sender = userEntityService.getUserByUserDetails(userDetails);
        UserEntity receiver = userEntityService.getById(dto.getReceiverId());
        return new MessageDto(messageService.sendMessage(sender, receiver, dto.getContent(), dto.getRepliedMessageId()));
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
                                                @AuthenticationPrincipal UserDetails userDetails)
            throws MessageNotFoundException, AccessDeniedException {
        UserEntity requester = userEntityService.getUserByUserDetails(userDetails);
        Message message = messageService.getById(messageId);
        return new MessageDto(messageService.edit(message, requester, dto.getNewContent()));
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
                              @AuthenticationPrincipal UserDetails userDetails) throws MessageNotFoundException, AccessDeniedException {
        UserEntity requester = userEntityService.getUserByUserDetails(userDetails);
        Message message = messageService.getById(messageId);
        messageService.delete(message, requester);
    }
}
