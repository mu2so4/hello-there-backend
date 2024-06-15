package ru.nsu.ccfit.muratov.hello.there.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.muratov.hello.there.dto.post.PostEditRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.post.PostRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Group;
import ru.nsu.ccfit.muratov.hello.there.entity.Post;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.AccessDeniedException;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.exception.ResourceNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.repository.PostRepository;
import ru.nsu.ccfit.muratov.hello.there.service.GroupService;
import ru.nsu.ccfit.muratov.hello.there.service.PostService;

import java.util.Date;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final GroupService groupService;
    @Value("${data.group.post.expiration}")
    private long expiration;

    public PostServiceImpl(PostRepository postRepository, GroupService groupService) {
        this.postRepository = postRepository;
        this.groupService = groupService;
    }

    @Override
    public Post findById(Integer id) throws ResourceNotFoundException {
        return postRepository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("Post not found"));
    }

    @Override
    public Page<Post> getGroupPosts(Integer groupId, int pageNumber, int pageSize, UserEntity requester)
            throws AccessDeniedException, GroupNotFoundException {
        Group group = groupService.getById(groupId);
        if(groupService.isBlacklisted(group, requester)) {
            throw new AccessDeniedException("Cannot access group posts blocked a user");
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createTime").descending());
        return postRepository.findByGroup(group, pageable);
    }

    @Override
    public Post create(PostRequestDto dto, UserEntity requester) throws AccessDeniedException, GroupNotFoundException {
        Group group = groupService.getById(dto.getGroupId());
        if(!groupService.checkOwner(group, requester)) {
            throw new AccessDeniedException("Only the group owner can post");
        }
        Post post = new Post();
        post.setContent(dto.getContent());
        post.setGroup(group);
        post.setCreateTime(new Date());
        return postRepository.save(post);
    }

    @Override
    public Post getSinglePost(Integer id, UserEntity requester) throws AccessDeniedException, ResourceNotFoundException {
        Post post = findById(id);
        if(groupService.isBlacklisted(post.getGroup(), requester)) {
            throw new AccessDeniedException("Cannot access group posts blocked a user");
        }
        return post;
    }

    @Override
    public Post update(Integer postId, PostEditRequestDto dto, UserEntity requester) throws AccessDeniedException, ResourceNotFoundException {
        Post post = findById(postId);
        Group group = post.getGroup();
        if(!groupService.checkOwner(group, requester)) {
            throw new AccessDeniedException("Only the group owner can edit post");
        }
        Date editTime = new Date();
        if(editTime.getTime() - post.getCreateTime().getTime() > expiration) {
            throw new AccessDeniedException("Post edit time limit expired");
        }
        post.setContent(dto.getNewContent());
        post.setLastEditTime(editTime);
        return postRepository.save(post);
    }

    @Override
    public void delete(Integer postId, UserEntity requester) throws AccessDeniedException, ResourceNotFoundException {
        Post post = findById(postId);
        Group group = post.getGroup();
        if(!groupService.checkOwner(group, requester)) {
            throw new AccessDeniedException("Only the group owner can delete post");
        }
        postRepository.delete(post);
    }
}
