package ru.nsu.ccfit.muratov.hello.there.entity.message;

import jakarta.persistence.*;
import lombok.Data;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;

@Data
@Entity(name = "Private_messages")
public class PrivateMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Message message;

    @ManyToOne
    @JoinColumn(name = "receiver")
    private UserEntity receiver;
}
