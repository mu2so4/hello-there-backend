package ru.nsu.ccfit.muratov.hello.there.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ccfit.muratov.hello.there.dto.post.PostDto;
import ru.nsu.ccfit.muratov.hello.there.dto.post.PostRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Group;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.AccessDeniedException;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.service.GroupService;
import ru.nsu.ccfit.muratov.hello.there.service.PostService;
import ru.nsu.ccfit.muratov.hello.there.service.UserEntityService;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Group posts")
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private UserEntityService userService;

    @Operation(
            summary = "Publish a new post in the group.",
            description = "Publish a new post in the group. Only the group owner can publish posts."
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Success",
                    responseCode = "201"
            ),
            @ApiResponse(
                    description = "No valid parameters set",
                    responseCode = "400",
                    content = @Content
            ),
            @ApiResponse(
                    description = "Unauthorized",
                    responseCode = "401",
                    content = @Content
            ),
            @ApiResponse(
                    description = "Access denied",
                    responseCode = "403",
                    content = @Content
            ),
            @ApiResponse(
                    description = "Group not found",
                    responseCode = "404",
                    content = @Content
            )
    })
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public PostDto createNewPost(@RequestBody PostRequestDto dto,
                                 @AuthenticationPrincipal UserDetails userDetails)
            throws GroupNotFoundException, AccessDeniedException {
        UserEntity requester = userService.getUserByUserDetails(userDetails);
        Group group = groupService.getById(dto.getGroupId());
        return new PostDto(postService.create(group, dto.getContent(), requester));
    }
}
