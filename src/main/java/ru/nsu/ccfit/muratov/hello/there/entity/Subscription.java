package ru.nsu.ccfit.muratov.hello.there.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.nsu.ccfit.muratov.hello.there.entity.id.SubscriptionId;

import java.util.Date;

@Data
@Entity(name = "Subscriptions")
public class Subscription {
    @EmbeddedId
    private SubscriptionId id;

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @MapsId("subscriberId")
    @JoinColumn(name = "subscriber")
    private UserEntity subscriber;

    private Date subscriptionTime;
}
