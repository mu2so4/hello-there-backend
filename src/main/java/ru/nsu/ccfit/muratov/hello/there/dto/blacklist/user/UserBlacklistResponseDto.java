package ru.nsu.ccfit.muratov.hello.there.dto.blacklist.user;

import lombok.Data;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;

@Data
public class UserBlacklistResponseDto {
    private int blockerId;
    private int blockedId;

    public UserBlacklistResponseDto(UserEntity blocker, UserEntity blocked) {
        blockerId = blocker.getId();
        blockedId = blocked.getId();
    }
}
