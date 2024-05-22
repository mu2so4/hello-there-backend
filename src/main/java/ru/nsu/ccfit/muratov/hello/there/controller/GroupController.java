package ru.nsu.ccfit.muratov.hello.there.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.nsu.ccfit.muratov.hello.there.dto.GroupCreateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.GroupDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Group;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.repository.GroupRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.UserRepository;

import java.util.Date;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/groups")
@Tag(name = "Group management")
public class GroupController {
    private static final Logger logger = Logger.getLogger(GroupController.class.getCanonicalName());

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;

    @Operation(
            summary = "Fetch group list",
            description = "Get all groups of the social network"
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Success",
                    responseCode = "200"
            ),
            @ApiResponse(
                    description = "Unauthorized",
                    responseCode = "401",
                    content = @Content
            )

    })
    @GetMapping
    public void getAllGroups() {
        //todo something
    }


    @Operation(
            summary = "Get a specific group",
            description = "Gets a group by group ID."
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Success",
                    responseCode = "200"
            ),
            @ApiResponse(
                    description = "Unauthorized",
                    responseCode = "401"
            ),
            @ApiResponse(
                    description = "Group not found",
                    responseCode = "404"
            )
    })
    @GetMapping("/{groupId}")
    public GroupDto getGroupById(@PathVariable int groupId) {
        try {
            Group group = groupRepository.getReferenceById(groupId);
            return new GroupDto(group);
        }
        catch(EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "group not found");
        }
    }


    @Operation(
            description = "Creates a new group",
            summary = "Create a new group"
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Success",
                    responseCode = "201"
            ),
            @ApiResponse(
                    description = "Invalid group parameters or some of them not set",
                    responseCode = "400"
            ),
            @ApiResponse(
                    description = "Unauthorized",
                    responseCode = "401"
            )
    })
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public GroupDto createGroup(@RequestBody GroupCreateRequestDto params, @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity owner = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("user not found"));
        Group group = new Group();
        group.setOwner(owner);
        group.setCreateTime(new Date());
        group.setName(params.getName());
        group.setDescription(params.getDescription());
        Group savedGroup = groupRepository.save(group);
        return new GroupDto(savedGroup);
    }


    @Operation(
            summary = "Update group data",
            description = "Updates some group params such as description by group ID."
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Success",
                    responseCode = "200"
            ),
            @ApiResponse(
                    description = "No valid parameters set",
                    responseCode = "400"
            ),
            @ApiResponse(
                    description = "Unauthorized",
                    responseCode = "401"
            ),
            @ApiResponse(
                    description = "User has no rights to edit this group",
                    responseCode = "403"
            ),
            @ApiResponse(
                    description = "Group not found",
                    responseCode = "404"
            )
    })
    @PatchMapping("/{groupId}")
    public Group updateGroup(@PathVariable("groupId") String groupId, @RequestBody Group newParams) {
        return newParams;
    }

    @Operation(
            summary = "Delete group",
            description = "Deletes group by its group ID."
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Success",
                    responseCode = "204"
            ),
            @ApiResponse(
                    description = "Unauthorized",
                    responseCode = "401"
            ),
            @ApiResponse(
                    description = "User has no rights to delete this group",
                    responseCode = "403"
            ),
            @ApiResponse(
                    description = "Group not found or already deleted",
                    responseCode = "404"
            )
    })
    @DeleteMapping("/{groupId}")
    public void updateGroup(@PathVariable("groupId") String groupId) {

    }
}
