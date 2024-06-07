package ru.nsu.ccfit.muratov.hello.there.repository.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.ccfit.muratov.hello.there.entity.message.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
}
