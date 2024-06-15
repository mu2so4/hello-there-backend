package ru.nsu.ccfit.muratov.hello.there.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.nsu.ccfit.muratov.hello.there.entity.id.GroupBlacklistId;

import java.util.Date;

@Data
@Table(name = "group_blacklist")
@Entity
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

    @Column(nullable = false)
    @Temporal(TemporalType.DATE) //todo WTF import returns without this @ java.sql.Timestamp instead of java.util.Date like simple-primary-keyed entities?
    private Date blockTime;
    @Column(nullable = false)
    private String reason;
}