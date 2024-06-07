package ru.nsu.ccfit.muratov.hello.there.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.ccfit.muratov.hello.there.entity.UserBlacklist;
import ru.nsu.ccfit.muratov.hello.there.entity.UserBlacklistId;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;

@Repository
public interface UserBlacklistRepository extends JpaRepository<UserBlacklist, UserBlacklistId> {
    Page<UserBlacklist> findByBlocker(UserEntity blocker, Pageable pageable);
}
