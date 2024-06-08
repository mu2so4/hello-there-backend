package ru.nsu.ccfit.muratov.hello.there.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.ccfit.muratov.hello.there.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
}
