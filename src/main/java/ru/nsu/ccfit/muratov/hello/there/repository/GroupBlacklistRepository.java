package ru.nsu.ccfit.muratov.hello.there.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.ccfit.muratov.hello.there.entity.*;

@Repository
public interface GroupBlacklistRepository extends JpaRepository<GroupBlacklist, GroupBlacklistId> {
    Page<GroupBlacklist> findByGroup(Group group, Pageable pageable);
}
