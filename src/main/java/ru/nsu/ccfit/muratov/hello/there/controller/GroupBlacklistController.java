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
import ru.nsu.ccfit.muratov.hello.there.dto.blacklist.group.GroupBlacklistDto;
import ru.nsu.ccfit.muratov.hello.there.dto.blacklist.group.GroupBlacklistRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Group;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.BadRequestException;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupAdminAccessDeniedException;
import ru.nsu.ccfit.muratov.hello.there.exception.ResourceNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.service.GroupService;
import ru.nsu.ccfit.muratov.hello.there.service.UserEntityService;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/blacklist")
@Tag(name = "Group blacklist")
public class GroupBlacklistController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private UserEntityService userEntityService;

    @Value("${data.user.blacklist.page.size}")
    private int pageSize;

    @Operation(
            summary = "Retrieve group's blacklist",
            description = "Retrieves group's blacklist. Blacklist is ordered by user IDs. " +
                    "Only the owner can access the blacklist."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Cannot access group's blacklist",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Group not found",
                    content = @Content
            )
    })
    @GetMapping(produces = "application/json")
    public List<GroupBlacklistDto> getBlacklist(@PathVariable int groupId,
                                                       @RequestParam(defaultValue = "0", name = "page") int pageNumber,
                                                       @AuthenticationPrincipal UserDetails userDetails) throws ResourceNotFoundException, GroupAdminAccessDeniedException {
        UserEntity requester = userEntityService.getUserByUserDetails(userDetails);
        Group group = groupService.getById(groupId);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
        return groupService.getBlacklist(group, requester, pageable).stream()
                .map(GroupBlacklistDto::new)
                .toList();
    }

    @Operation(
            summary = "Add user to user's blacklist",
            description = "Adds user to user's blacklist by their ID. " +
                    "Only the group's owner can perform this action."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Success"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Cannot access group's blacklist",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or group not found",
                    content = @Content
            )
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(code = HttpStatus.CREATED)
    public GroupBlacklistDto addToBlacklist(@RequestBody GroupBlacklistRequestDto dto,
                                            @PathVariable int groupId,
                                            @AuthenticationPrincipal UserDetails userDetails)
            throws ResourceNotFoundException, BadRequestException, GroupAdminAccessDeniedException {
        UserEntity requester = userEntityService.getUserByUserDetails(userDetails);
        Group group = groupService.getById(groupId);
        UserEntity blocked = userEntityService.getById(dto.getUserId());
        return new GroupBlacklistDto(groupService.addToBlacklist(group, blocked, dto.getReason(), requester));
    }

    @Operation(
            summary = "Remove user from group's blacklist",
            description = "Removes user from group's blacklist by their ID. " +
                    "Only the group's owner can perform this action."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Success"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Cannot access group's blacklist",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or group not found",
                    content = @Content
            )
    })
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromBlacklist(@PathVariable(name = "userId") int blockedId,
                                    @PathVariable int groupId,
                                    @AuthenticationPrincipal UserDetails userDetails)
            throws GroupAdminAccessDeniedException, ResourceNotFoundException {
        UserEntity requester = userEntityService.getUserByUserDetails(userDetails);
        Group group = groupService.getById(groupId);
        UserEntity blocked = userEntityService.getById(blockedId);
        groupService.removeFromBlacklist(group, blocked, requester);
    }
}
