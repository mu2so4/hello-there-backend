package ru.nsu.ccfit.muratov.hello.there.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.ccfit.muratov.hello.there.entity.Comment;
import ru.nsu.ccfit.muratov.hello.there.entity.Post;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findByPost(Post post, Pageable pageable);
}
