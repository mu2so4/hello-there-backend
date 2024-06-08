package ru.nsu.ccfit.muratov.hello.there.dto;

import lombok.Data;
import ru.nsu.ccfit.muratov.hello.there.entity.Group;
import ru.nsu.ccfit.muratov.hello.there.entity.Subscription;

import java.util.Date;

@Data
public class SubscriptionDto {
    private GroupDto group;
    private UserDto subscriber;
    private Date subscriptionTime;

    public SubscriptionDto(Subscription subscription) {
        this.group = new GroupDto(subscription.getGroup());
        this.subscriber = new UserDto(subscription.getSubscriber());
        this.subscriptionTime = subscription.getSubscriptionTime();
    }


    @Data
    public static class GroupDto {
        private int groupId;
        private String name;

        public GroupDto(Group group) {
            this.groupId = group.getId();
            this.name = group.getName();
        }
    }
}
