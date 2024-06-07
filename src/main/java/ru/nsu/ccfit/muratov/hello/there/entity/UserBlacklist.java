package ru.nsu.ccfit.muratov.hello.there.entity;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "user_blacklist")
public class UserBlacklist {
    @EmbeddedId
    private UserBlacklistId id;

    @ManyToOne
    @MapsId("blockerId")
    @JoinColumn(name = "blocker")
    private UserEntity blocker;

    @ManyToOne
    @MapsId("blockedId")
    @JoinColumn(name = "blocked")
    private UserEntity blocked;
}
