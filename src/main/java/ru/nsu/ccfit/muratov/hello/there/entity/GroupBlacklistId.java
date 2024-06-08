package ru.nsu.ccfit.muratov.hello.there.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class GroupBlacklistId implements Serializable {
    @Column(name = "group_id")
    private int groupId;

    @Column(name = "blocked")
    private int blockedUserId;
}
