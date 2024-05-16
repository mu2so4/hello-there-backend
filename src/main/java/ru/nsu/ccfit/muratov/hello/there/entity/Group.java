package ru.nsu.ccfit.muratov.hello.there.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity(name = "Groups")
public class Group {
    @Id
    @GeneratedValue
    private int groupId;

    private String groupName;
    private String description;
    private Date createTime;

    @ManyToOne
    @JoinColumn(name = "userId")
    private UserEntity owner;
}
