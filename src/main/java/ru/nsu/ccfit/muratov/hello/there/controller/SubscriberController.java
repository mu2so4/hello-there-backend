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
import ru.nsu.ccfit.muratov.hello.there.dto.SubscriptionDto;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.BadRequestException;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupBlacklistedException;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.service.GroupService;
import ru.nsu.ccfit.muratov.hello.there.service.UserEntityService;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/subscribers")
@Tag(name = "Group subscribers")
public class SubscriberController {
    @Autowired
    private UserEntityService userEntityService;
    @Autowired
    private GroupService subscriptionService;

    @Value("${data.groups.subscribers.page.size}")
    private int pageSize;

    @Operation(summary = "Retrieve group's subscriber list",
            description = "Retrieves group's subscriber list. The list is ordered by user IDs. " +
                    "A user cannot access the list if they are blocked by the group."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied for blacklisted user", content = @Content),
            @ApiResponse(responseCode = "404", description = "Group not found", content = @Content)
    })
    @GetMapping(produces = "application/json")
    public List<SubscriptionDto> getSubscribers(@PathVariable int groupId,
                                                @RequestParam(name = "page", defaultValue = "0") int pageNumber,
                                                @AuthenticationPrincipal UserDetails userDetails)
            throws GroupNotFoundException, GroupBlacklistedException {
        UserEntity user = userEntityService.getUserByUserDetails(userDetails);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("subscriptionTime"));
        return subscriptionService.getSubscriberList(groupId, pageable, user).stream()
                .map(SubscriptionDto::new)
                .toList();
    }


    @Operation(summary = "Subscribe on a group",
            description = "Subscribes on a group. " +
                    "A user cannot subscribe if they are blocked by the group."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied for blacklisted user", content = @Content),
            @ApiResponse(responseCode = "404", description = "Group not found", content = @Content)
    })
    @PostMapping(produces = "application/json")
    @ResponseStatus(code = HttpStatus.CREATED)
    public SubscriptionDto subscribe(@PathVariable int groupId,
                                     @AuthenticationPrincipal UserDetails userDetails)
            throws GroupBlacklistedException, GroupNotFoundException {
        UserEntity user = userEntityService.getUserByUserDetails(userDetails);
        return new SubscriptionDto(subscriptionService.subscribe(groupId, user));
    }

    @Operation(summary = "Unsubscribe from a group",
            description = "Unsubscribes from a group. " +
                    "A user cannot subscribe if they are the group's owner."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Group not found", content = @Content)
    })
    @DeleteMapping
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable int groupId,
                            @AuthenticationPrincipal UserDetails userDetails)
            throws GroupNotFoundException, BadRequestException {
        UserEntity user = userEntityService.getUserByUserDetails(userDetails);
        subscriptionService.unsubscribe(groupId, user);
    }
}
