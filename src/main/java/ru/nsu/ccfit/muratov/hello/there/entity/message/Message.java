package ru.nsu.ccfit.muratov.hello.there.entity.message;

import jakarta.persistence.*;
import lombok.Data;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;

import java.util.Date;

@Data
@Entity(name = "Messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sender")
    private UserEntity sender;

    private String content;
    private Date sendTime;
    private Date lastEditTime;

    @ManyToOne
    @JoinColumn(name = "replied_message")
    private Message repliedMessage;
}
