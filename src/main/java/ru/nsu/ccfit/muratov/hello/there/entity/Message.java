package ru.nsu.ccfit.muratov.hello.there.entity;

import jakarta.persistence.*;
import lombok.Data;

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
    @ManyToOne
    @JoinColumn(name = "receiver")
    private UserEntity receiver;

    private String content;
    private Date sendTime;
    private Date lastEditTime;

    @ManyToOne
    @JoinColumn(name = "replied_message")
    private Message repliedMessage;
}
