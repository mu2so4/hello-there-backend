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
import ru.nsu.ccfit.muratov.hello.there.dto.post.PostEditRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.post.PostRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.AccessDeniedException;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.exception.ResourceNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.service.PostService;
import ru.nsu.ccfit.muratov.hello.there.service.UserEntityService;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Group posts")
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private UserEntityService userService;

    @Operation(summary = "Retrieve a single group post",
            description = "Retrieves a single group post. " +
                    "Blocked users cannot view group posts."
    )
    @ApiResponses({
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Bad post ID", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "User blocked by group", responseCode = "403", content = @Content),
            @ApiResponse(description = "Group not found", responseCode = "404", content = @Content)
    })
    @GetMapping(value = "/{postId}", produces = "application/json")
    public PostDto getPost(@PathVariable int postId,
                           @AuthenticationPrincipal UserDetails userDetails)
            throws AccessDeniedException, ResourceNotFoundException {
        UserEntity user = userService.getUserByUserDetails(userDetails);
        return new PostDto(postService.getSinglePost(postId, user));
    }

    @Operation(summary = "Publish a new post in the group",
            description = "Publishes a new post in the group. Only the group owner can publish posts."
    )
    @ApiResponses({
            @ApiResponse(description = "Success", responseCode = "201"),
            @ApiResponse(description = "No valid parameters set", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Access denied", responseCode = "403", content = @Content),
            @ApiResponse(description = "Group not found", responseCode = "404", content = @Content)
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(code = HttpStatus.CREATED)
    public PostDto createNewPost(@RequestBody PostRequestDto dto,
                                 @AuthenticationPrincipal UserDetails userDetails)
            throws GroupNotFoundException, AccessDeniedException {
        UserEntity requester = userService.getUserByUserDetails(userDetails);
        return new PostDto(postService.create(dto, requester));
    }

    @Operation(summary = "Edit group post content",
            description = "Edits group post content. Only the group owner can edit group posts."
    )
    @ApiResponses({
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "No valid parameters set", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Access denied", responseCode = "403", content = @Content),
            @ApiResponse(description = "Post not found", responseCode = "404", content = @Content)
    })
    @PatchMapping(value = "/{postId}", consumes = "application/json", produces = "application/json")
    public PostDto editPost(@RequestBody PostEditRequestDto dto,
                            @PathVariable int postId,
                            @AuthenticationPrincipal UserDetails userDetails) throws ResourceNotFoundException, AccessDeniedException {
        UserEntity requester = userService.getUserByUserDetails(userDetails);
        return new PostDto(postService.update(postId, dto, requester));
    }

    @Operation(summary = "Delete group post",
            description = "Deletes group post. Only the group owner can delete group posts."
    )
    @ApiResponses({
            @ApiResponse(description = "Success", responseCode = "204"),
            @ApiResponse(description = "Bad group ID", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Access denied", responseCode = "403", content = @Content),
            @ApiResponse(description = "Post not found or already deleted", responseCode = "404", content = @Content)
    })
    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable int postId,
                           @AuthenticationPrincipal UserDetails userDetails) throws ResourceNotFoundException, AccessDeniedException {
        UserEntity requester = userService.getUserByUserDetails(userDetails);
        postService.delete(postId, requester);
    }
}
