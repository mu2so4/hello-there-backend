package ru.nsu.ccfit.muratov.hello.there.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.ccfit.muratov.hello.there.entity.Group;
import ru.nsu.ccfit.muratov.hello.there.entity.Subscription;
import ru.nsu.ccfit.muratov.hello.there.entity.id.SubscriptionId;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {
    List<Subscription> findByGroup(Group group, Pageable pageable);
}
