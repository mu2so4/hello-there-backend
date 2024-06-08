package ru.nsu.ccfit.muratov.hello.there.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nsu.ccfit.muratov.hello.there.entity.Message;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT m FROM Messages m WHERE " +
            "(m.sender = :user1 AND m.receiver = :user2) OR " +
            "(m.sender = :user2 AND m.receiver = :user1)")
    Page<Message> getCorrespondenceByUsers(@Param("user1") UserEntity user1, @Param("user2") UserEntity user2, Pageable pageable);
}
