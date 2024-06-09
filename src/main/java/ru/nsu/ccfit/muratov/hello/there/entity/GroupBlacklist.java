package ru.nsu.ccfit.muratov.hello.there.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.nsu.ccfit.muratov.hello.there.entity.id.GroupBlacklistId;

import java.util.Date;

@Data
@Entity(name = "group_blacklist")
public class GroupBlacklist {
    @EmbeddedId
    private GroupBlacklistId id;

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @MapsId("blockedUserId")
    @JoinColumn(name = "blocked")
    private UserEntity blockedUser;

    private Date blockTime;
    private String reason;
}