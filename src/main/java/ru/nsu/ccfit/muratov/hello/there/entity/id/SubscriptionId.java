package ru.nsu.ccfit.muratov.hello.there.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionId {
    @Column(name = "group_id")
    private int groupId;

    @Column(name = "subscriber")
    private int subscriberId;
}
