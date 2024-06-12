package ru.nsu.ccfit.muratov.hello.there.service;

import org.springframework.data.domain.Page;
import ru.nsu.ccfit.muratov.hello.there.entity.Group;
import ru.nsu.ccfit.muratov.hello.there.entity.Post;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.AccessDeniedException;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.exception.ResourceNotFoundException;

public interface PostService {
    Post findById(Integer id) throws ResourceNotFoundException;

    Page<Post> getGroupPosts(Integer groupId, int pageNumber, int pageSize, UserEntity requester) throws AccessDeniedException, GroupNotFoundException;

    Post create(Group group, String content, UserEntity requester) throws AccessDeniedException;
    Post getSinglePost(Integer id, UserEntity requester) throws AccessDeniedException, ResourceNotFoundException;
    Post update(Post post, String newContent, UserEntity requester) throws AccessDeniedException;
    void delete(Post post, UserEntity requester) throws AccessDeniedException;
}
