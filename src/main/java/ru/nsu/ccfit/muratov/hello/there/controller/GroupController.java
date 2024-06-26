package ru.nsu.ccfit.muratov.hello.there.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ccfit.muratov.hello.there.dto.group.GroupCreateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.group.GroupDto;
import ru.nsu.ccfit.muratov.hello.there.dto.group.GroupUpdateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.post.PostDto;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.AccessDeniedException;
import ru.nsu.ccfit.muratov.hello.there.exception.BadRequestException;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupAdminAccessDeniedException;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.service.GroupService;
import ru.nsu.ccfit.muratov.hello.there.service.PostService;
import ru.nsu.ccfit.muratov.hello.there.service.UserEntityService;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/groups")
@Tag(name = "Group management")
public class GroupController {
    private static final Logger logger = Logger.getLogger(GroupController.class.getCanonicalName());

    @Autowired
    private UserEntityService userEntityService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private PostService postService;

    @Value("${data.group.page.size}")
    private int pageSize;

    @Operation(summary = "Fetch group list",
            description = "Get all groups of the social network."
    )
    @ApiResponses({
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content)
    })
    @GetMapping
    public List<GroupDto> getAllGroups(@RequestParam(name = "page", defaultValue = "0") int pageNumber) {
        return groupService.getGroupList(pageNumber, pageSize).stream()
                .map(GroupDto::new)
                .toList();
    }


    @Operation(summary = "Get a specific group",
            description = "Gets a group by group ID."
    )
    @ApiResponses({
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Group not found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Group was deleted", responseCode = "410", content = @Content)
    })
    @GetMapping("/{groupId}")
    public GroupDto getGroupById(@PathVariable int groupId) throws GroupNotFoundException {
        return new GroupDto(groupService.getById(groupId));
    }


    @Operation(description = "Creates a new group.",
            summary = "Create a new group"
    )
    @ApiResponses({
            @ApiResponse(description = "Success", responseCode = "201"),
            @ApiResponse(description = "Bad group parameters", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content)
    })
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public GroupDto createGroup(@RequestBody GroupCreateRequestDto params, @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity owner = userEntityService.getUserByUserDetails(userDetails);
        return new GroupDto(groupService.create(params, owner));
    }


    @Operation(summary = "Update group data",
            description = "Updates some group params such as description by group ID."
    )
    @ApiResponses({
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "No valid parameters set", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "User has no rights to edit this group", responseCode = "403", content = @Content),
            @ApiResponse(description = "Group not found", responseCode = "404", content = @Content)
    })
    @PatchMapping("/{groupId}")
    public GroupDto updateGroup(@PathVariable int groupId, @RequestBody GroupUpdateRequestDto newParams, @AuthenticationPrincipal UserDetails userDetails)
            throws GroupNotFoundException, BadRequestException, GroupAdminAccessDeniedException {
        UserEntity requester = userEntityService.getUserByUserDetails(userDetails);
        return new GroupDto(groupService.update(groupId, newParams, requester));
    }

    @Operation(summary = "Delete group",
            description = "Deletes group by its group ID."
    )
    @ApiResponses({
            @ApiResponse(description = "Success", responseCode = "204"),
            @ApiResponse(description = "Bad group ID", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "User has no rights to delete this group", responseCode = "403", content = @Content),
            @ApiResponse(description = "Group not found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Group already deleted", responseCode = "410", content = @Content)
    })
    @DeleteMapping("/{groupId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updateGroup(@PathVariable("groupId") int groupId, @AuthenticationPrincipal UserDetails userDetails)
            throws GroupNotFoundException, GroupAdminAccessDeniedException {
        UserEntity requester = userEntityService.getUserByUserDetails(userDetails);
        groupService.delete(groupId, requester);
    }

    @Operation(summary = "Retrieve group posts",
            description = "Retrieves group posts. Posts are ordered by create time descending. " +
                    "Blocked users cannot view group posts."
    )
    @ApiResponses({
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Bad group ID", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "User blocked by group", responseCode = "403", content = @Content),
            @ApiResponse(description = "Group not found", responseCode = "404", content = @Content)
    })
    @GetMapping(value = "/{groupId}/posts", produces = "application/json")
    public List<PostDto> getPosts(@PathVariable("groupId") int groupId,
                                  @RequestParam(name = "page", defaultValue = "0") int pageNumber,
                                  @AuthenticationPrincipal UserDetails userDetails) throws GroupNotFoundException, AccessDeniedException {
        UserEntity requester = userEntityService.getUserByUserDetails(userDetails);
        return postService.getGroupPosts(groupId, pageNumber, pageSize, requester).stream()
                .map(PostDto::new)
                .toList();
    }
}
