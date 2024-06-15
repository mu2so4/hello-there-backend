package ru.nsu.ccfit.muratov.hello.there.entity.id;


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
public class UserBlacklistId implements Serializable {
    @Column(name = "blocker")
    private int blockerId;

    @Column(name = "blocked")
    private int blockedId;
}
