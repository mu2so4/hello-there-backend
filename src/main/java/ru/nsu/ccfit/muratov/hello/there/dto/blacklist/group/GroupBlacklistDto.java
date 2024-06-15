package ru.nsu.ccfit.muratov.hello.there.dto.blacklist.group;

import lombok.Data;
import ru.nsu.ccfit.muratov.hello.there.dto.UserDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Group;
import ru.nsu.ccfit.muratov.hello.there.entity.GroupBlacklist;

import java.util.Date;

@Data
public class GroupBlacklistDto {
    private UserDto user;
    private Date blockTime;
    private String reason;

    public GroupBlacklistDto(GroupBlacklist groupBlacklist) {
        this.user = new UserDto(groupBlacklist.getBlockedUser());
        this.blockTime = groupBlacklist.getBlockTime();
        this.reason = groupBlacklist.getReason();
    }
}
