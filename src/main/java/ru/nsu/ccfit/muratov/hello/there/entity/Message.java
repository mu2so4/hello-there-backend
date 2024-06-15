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
    @JoinColumn(name = "sender", nullable = false)
    private UserEntity sender;
    @ManyToOne
    @JoinColumn(name = "receiver", nullable = false)
    private UserEntity receiver;

    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private Date sendTime;
    @Column
    private Date lastEditTime;

    @ManyToOne
    @JoinColumn(name = "replied_message")
    private Message repliedMessage;
}
