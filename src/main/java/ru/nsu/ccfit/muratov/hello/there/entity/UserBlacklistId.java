package ru.nsu.ccfit.muratov.hello.there.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class UserBlacklistId implements Serializable {
    @Column(name = "blocker")
    private int blockerId;

    @Column(name = "blocked")
    private int blockedId;
}
