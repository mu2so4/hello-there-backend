package ru.nsu.ccfit.muratov.hello.there.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity(name = "Groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Date createTime;
    @Column(nullable = false)
    private boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "owner", nullable = false)
    private UserEntity owner;
}
