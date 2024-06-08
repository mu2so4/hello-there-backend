package ru.nsu.ccfit.muratov.hello.there.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
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
import ru.nsu.ccfit.muratov.hello.there.dto.blacklist.group.GroupBlacklistDto;
import ru.nsu.ccfit.muratov.hello.there.dto.blacklist.group.GroupBlacklistRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Group;
import ru.nsu.ccfit.muratov.hello.there.entity.GroupBlacklist;
import ru.nsu.ccfit.muratov.hello.there.entity.GroupBlacklistId;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.repository.GroupBlacklistRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.GroupRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.UserBlacklistRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.UserRepository;
import ru.nsu.ccfit.muratov.hello.there.service.UserEntityService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/blacklist")
@Tag(name = "Group blacklist")
public class GroupBlacklistController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupBlacklistRepository groupBlacklistRepository;
    @Autowired
    private UserEntityService userEntityService;
    @Autowired
    private GroupRepository groupRepository;

    @Value("${data.user.blacklist.page.size}")
    private int pageSize;

    /*@Operation(
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
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity requester = userEntityService.getUserByUserDetails(userDetails);
        if(!groupRepository.existsById(groupId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
        }
        Group group = groupRepository.getReferenceById(groupId);
        if(!requester.equals(group.getOwner())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access not being the owner");
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
        return groupBlacklistRepository.findByGroup(group, pageable).stream()
                .map(GroupBlacklistDto::new)
                .toList();
    }*/

    @Operation(
            summary = "Add user to user's blacklist",
            description = "Adds user to user's blacklist by their ID."
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
                    responseCode = "404",
                    description = "User to be blacklisted not found",
                    content = @Content
            )
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(code = HttpStatus.CREATED)
    public GroupBlacklistDto addToBlacklist(@RequestBody GroupBlacklistRequestDto dto,
                                            @PathVariable int groupId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity requester = userEntityService.getUserByUserDetails(userDetails);
        if(!groupRepository.existsById(groupId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
        }
        int blockedId = dto.getUserId();
        if(!userRepository.existsById(blockedId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        Group group = groupRepository.getReferenceById(groupId);
        UserEntity owner = group.getOwner();
        if(!requester.equals(owner)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot manage group blacklist not being the owner");
        }
        UserEntity blocked = userRepository.getReferenceById(blockedId);
        if(blocked.equals(owner)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attempt to add themselves to blacklist");
        }
        GroupBlacklistId groupBlacklistId = new GroupBlacklistId(group.getId(), blockedId);
        GroupBlacklist groupBlacklist = new GroupBlacklist();
        groupBlacklist.setId(groupBlacklistId);
        groupBlacklist.setGroup(group);
        groupBlacklist.setBlockedUser(blocked);
        groupBlacklist.setBlockTime(new Date());
        groupBlacklist.setReason(dto.getReason());

        return new GroupBlacklistDto(groupBlacklistRepository.save(groupBlacklist));
    }

    @Operation(
            summary = "Remove user from user's blacklist",
            description = "Removes user from user's blacklist by their ID."
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
                    responseCode = "404",
                    description = "User to be unblacklisted not found",
                    content = @Content
            )
    })
    @DeleteMapping("/{userId}")
    public void removeFromBlacklist(@PathVariable int userId, @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity blocker = userEntityService.getUserByUserDetails(userDetails);
        try {
            UserEntity blocked = userRepository.getReferenceById(userId);
            blocker.getBlacklist().remove(blocked);
        }
        catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.save(blocker);
    }
}
