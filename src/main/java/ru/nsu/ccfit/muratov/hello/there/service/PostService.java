package ru.nsu.ccfit.muratov.hello.there.service;

import org.springframework.data.domain.Page;
import ru.nsu.ccfit.muratov.hello.there.dto.post.PostEditRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.post.PostRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Group;
import ru.nsu.ccfit.muratov.hello.there.entity.Post;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.AccessDeniedException;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.exception.ResourceNotFoundException;

public interface PostService {
    Post findById(Integer id) throws ResourceNotFoundException;

    Page<Post> getGroupPosts(Integer groupId, int pageNumber, int pageSize, UserEntity requester) throws AccessDeniedException, GroupNotFoundException;

    Post create(PostRequestDto dto, UserEntity requester) throws AccessDeniedException, GroupNotFoundException;
    Post getSinglePost(Integer postId, UserEntity requester) throws AccessDeniedException, ResourceNotFoundException;
    Post update(Integer postId, PostEditRequestDto dto, UserEntity requester) throws AccessDeniedException, ResourceNotFoundException;
    void delete(Integer postId, UserEntity requester) throws AccessDeniedException, ResourceNotFoundException;
}
