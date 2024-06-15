package ru.nsu.ccfit.muratov.hello.there.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.muratov.hello.there.dto.comment.CommentCreateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.comment.CommentUpdateRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Comment;
import ru.nsu.ccfit.muratov.hello.there.entity.Post;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.AccessDeniedException;
import ru.nsu.ccfit.muratov.hello.there.exception.BadRequestException;
import ru.nsu.ccfit.muratov.hello.there.exception.ResourceNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.repository.CommentRepository;
import ru.nsu.ccfit.muratov.hello.there.service.CommentService;
import ru.nsu.ccfit.muratov.hello.there.service.GroupService;
import ru.nsu.ccfit.muratov.hello.there.service.PostService;

import java.util.Date;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final GroupService groupService;
    private final PostService postService;

    public CommentServiceImpl(CommentRepository commentRepository, GroupService groupService, PostService postService) {
        this.commentRepository = commentRepository;
        this.groupService = groupService;
        this.postService = postService;
    }

    @Override
    public Comment getById(Integer commentId) throws ResourceNotFoundException {
        return commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }

    @Override
    public Comment create(CommentCreateRequestDto dto, Integer postId, UserEntity requester)
            throws ResourceNotFoundException, AccessDeniedException, BadRequestException {
        Post post = postService.findById(postId);
        if(isNotAccessible(post, requester)) {
            throw new AccessDeniedException("Cannot create a comment on a group post if group blocked you");
        }
        Comment comment = new Comment();
        comment.setCommenter(requester);
        comment.setCreateTime(new Date());
        comment.setPost(post);
        comment.setContent(dto.getContent());
        return commentRepository.save(comment);
    }

    @Override
    public Comment update(Integer commentId, CommentUpdateRequestDto dto, UserEntity requester)
            throws ResourceNotFoundException, AccessDeniedException, BadRequestException {
        Comment comment = getById(commentId);
        if(isNotAccessible(comment.getPost(), requester)) {
            throw new AccessDeniedException("Cannot edit a comment on a group post if group blocked you");
        }
        if(!requester.equals(comment.getCommenter())) {
            throw new AccessDeniedException("Cannot edit other's comment");
        }
        //todo check expiration
        comment.setLastEditTime(new Date());
        comment.setContent(dto.getNewContent());
        return commentRepository.save(comment);
    }

    @Override
    public Comment getComment(Integer commentId, UserEntity requester) throws ResourceNotFoundException, AccessDeniedException {
        Comment comment = getById(commentId);
        if(isNotAccessible(comment.getPost(), requester)) {
            throw new AccessDeniedException("Cannot access a comment on a group post if group blocked you");
        }
        return comment;
    }

    @Override
    public void delete(Integer commentId, UserEntity requester) throws ResourceNotFoundException, AccessDeniedException, BadRequestException {
        Comment comment = getById(commentId);
        UserEntity owner = comment.getPost().getGroup().getOwner();
        if(!(requester.equals(comment.getCommenter()) || requester.equals(owner))) {
            throw new AccessDeniedException("Cannot delete other's comment");
        }
        commentRepository.delete(comment);
    }

    @Override
    public Page<Comment> getPostComments(Integer postId, Pageable pageable, UserEntity requester)
            throws ResourceNotFoundException, AccessDeniedException, BadRequestException {
        Post post = postService.findById(postId);
        if(isNotAccessible(post, requester)) {
            throw new AccessDeniedException("Cannot retrieve comments on a group post if group blocked you");
        }
        return commentRepository.findByPost(post, pageable);
    }

    private boolean isNotAccessible(Post post, UserEntity user) {
        return groupService.isBlacklisted(post.getGroup(), user);
    }
}
