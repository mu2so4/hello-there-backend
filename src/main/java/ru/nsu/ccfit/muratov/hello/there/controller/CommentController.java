package ru.nsu.ccfit.muratov.hello.there.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ccfit.muratov.hello.there.dto.comment.CommentCreateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.comment.CommentDto;
import ru.nsu.ccfit.muratov.hello.there.dto.comment.CommentUpdateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.AccessDeniedException;
import ru.nsu.ccfit.muratov.hello.there.exception.BadRequestException;
import ru.nsu.ccfit.muratov.hello.there.exception.ResourceNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.service.CommentService;
import ru.nsu.ccfit.muratov.hello.there.service.UserEntityService;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@Tag(name = "Post comments")
public class CommentController {
    private final UserEntityService userService;
    private final CommentService commentService;

    private static final int COMMENT_PAGE_SIZE = 5;

    public CommentController(UserEntityService userService, CommentService commentService) {
        this.userService = userService;
        this.commentService = commentService;
    }


    @Operation(summary = "Retrieve post comments",
            description = "Retrieves post comments. Comments are ordered by create time. " +
                    "Blocked users cannot view group posts."
    )
    @ApiResponses({
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Bad group ID", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "User blocked by group", responseCode = "403", content = @Content),
            @ApiResponse(description = "Post not found", responseCode = "404", content = @Content)
    })
    @GetMapping
    public List<CommentDto> getComments(@PathVariable int postId,
                                        @RequestParam(name = "page", defaultValue = "0") int pageNumber,
                                        @AuthenticationPrincipal UserDetails userDetails)
            throws AccessDeniedException, BadRequestException, ResourceNotFoundException {
        UserEntity user = userService.getUserByUserDetails(userDetails);
        Pageable pageable = PageRequest.of(pageNumber, COMMENT_PAGE_SIZE, Sort.by("createTime"));
        return commentService.getPostComments(postId, pageable, user).stream()
                .map(CommentDto::new)
                .toList();
    }


    @Operation(summary = "Publish a new comment in the post",
            description = "Publishes a new comment in the post. Only the group owner can publish posts."
    )
    @ApiResponses({
            @ApiResponse(description = "Success", responseCode = "201"),
            @ApiResponse(description = "No valid parameters set", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Access denied", responseCode = "403", content = @Content),
            @ApiResponse(description = "Post not found", responseCode = "404", content = @Content)
    })
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CommentDto postComment(@PathVariable int postId,
                                  @RequestBody CommentCreateRequestDto dto,
                                  @AuthenticationPrincipal UserDetails userDetails) throws AccessDeniedException, BadRequestException, ResourceNotFoundException {
        UserEntity user = userService.getUserByUserDetails(userDetails);
        return new CommentDto(commentService.create(dto, postId, user));
    }


    @Operation(summary = "Update comment",
            description = "Updates a comment. Only its commenter can do it if they were not blocked after creating."
    )
    @ApiResponses({
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "No valid parameters set", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Access denied", responseCode = "403", content = @Content),
            @ApiResponse(description = "Comment not found", responseCode = "404", content = @Content)
    })
    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable int commentId,
                                  @RequestBody CommentUpdateRequestDto dto,
                                  @AuthenticationPrincipal UserDetails userDetails) throws AccessDeniedException, BadRequestException, ResourceNotFoundException {
        UserEntity user = userService.getUserByUserDetails(userDetails);
        return new CommentDto(commentService.update(commentId, dto, user));
    }


    @Operation(summary = "Delete comment",
            description = "Deletes a comment. Only the comment author can delete their comment if they were not blocked. " +
                    "Note that the group owner can delete any comment of any post group regardless of comment author."
    )
    @ApiResponses({
            @ApiResponse(description = "Success", responseCode = "204"),
            @ApiResponse(description = "Bad group ID", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Access denied", responseCode = "403", content = @Content),
            @ApiResponse(description = "Comment not found or already deleted", responseCode = "404", content = @Content)
    })
    @DeleteMapping("/{commentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable int commentId,
                                    @AuthenticationPrincipal UserDetails userDetails) throws AccessDeniedException, BadRequestException, ResourceNotFoundException {
        UserEntity user = userService.getUserByUserDetails(userDetails);
        commentService.delete(commentId, user);
    }
}
