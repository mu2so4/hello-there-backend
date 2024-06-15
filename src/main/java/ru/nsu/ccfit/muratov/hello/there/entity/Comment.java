package ru.nsu.ccfit.muratov.hello.there.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity(name = "Comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private Date createTime;
    private Date lastEditTime;

    @ManyToOne
    @JoinColumn(name = "post", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "commenter", nullable = false)
    private UserEntity commenter;
}
