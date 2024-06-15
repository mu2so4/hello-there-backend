package ru.nsu.ccfit.muratov.hello.there.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.nsu.ccfit.muratov.hello.there.dto.comment.CommentCreateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.comment.CommentUpdateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Comment;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.AccessDeniedException;
import ru.nsu.ccfit.muratov.hello.there.exception.BadRequestException;
import ru.nsu.ccfit.muratov.hello.there.exception.ResourceNotFoundException;

public interface CommentService {
    Comment getById(Integer commentId) throws ResourceNotFoundException;

    Comment create(CommentCreateRequestDto dto, Integer postId, UserEntity requester)
            throws ResourceNotFoundException, AccessDeniedException, BadRequestException;

    Comment update(Integer commentId, CommentUpdateRequestDto dto, UserEntity requester)
            throws ResourceNotFoundException, AccessDeniedException, BadRequestException;

    Comment getComment(Integer commentId, UserEntity requester)
            throws ResourceNotFoundException, AccessDeniedException;

    void delete(Integer commentId, UserEntity requester)
            throws ResourceNotFoundException, AccessDeniedException, BadRequestException;

    Page<Comment> getPostComments(Integer postId, Pageable pageable, UserEntity requester)
            throws ResourceNotFoundException, AccessDeniedException, BadRequestException;
}
