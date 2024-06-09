package ru.nsu.ccfit.muratov.hello.there.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.muratov.hello.there.entity.Group;
import ru.nsu.ccfit.muratov.hello.there.entity.Post;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.AccessDeniedException;
import ru.nsu.ccfit.muratov.hello.there.exception.ResourceNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.repository.PostRepository;
import ru.nsu.ccfit.muratov.hello.there.service.GroupService;
import ru.nsu.ccfit.muratov.hello.there.service.PostService;

import java.util.Date;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private GroupService groupService;
    @Value("${data.group.post.expiration}")
    private long expiration;

    @Override
    public Post findById(Integer id) throws ResourceNotFoundException {
        if(!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post not found");
        }
        return postRepository.getReferenceById(id);
    }

    @Override
    public Page<Post> getGroupPosts(Group group, Pageable pageable, UserEntity requester) throws AccessDeniedException {
        if(groupService.isBlacklisted(group, requester)) {
            throw new AccessDeniedException("Cannot access group posts blocked a user");
        }
        return postRepository.findByGroup(group, pageable);
    }

    @Override
    public Post create(Group group, String content, UserEntity requester) throws AccessDeniedException {
        if(!groupService.checkOwner(group, requester)) {
            throw new AccessDeniedException("Only the group owner can post");
        }
        Post post = new Post();
        post.setContent(content);
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
    public Post update(Post post, String newContent, UserEntity requester) throws AccessDeniedException {
        Group group = post.getGroup();
        if(!groupService.checkOwner(group, requester)) {
            throw new AccessDeniedException("Only the group owner can edit post");
        }
        Date editTime = new Date();
        if(editTime.getTime() - post.getCreateTime().getTime() > expiration) {
            throw new AccessDeniedException("Post edit time limit expired");
        }
        post.setContent(newContent);
        post.setLastEditTime(editTime);
        return postRepository.save(post);
    }

    @Override
    public void delete(Post post, UserEntity requester) throws AccessDeniedException {
        Group group = post.getGroup();
        if(!groupService.checkOwner(group, requester)) {
            throw new AccessDeniedException("Only the group owner can delete post");
        }
        postRepository.delete(post);
    }
}
