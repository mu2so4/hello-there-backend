package ru.nsu.ccfit.muratov.hello.there.dto.blacklist.group;

import lombok.Data;

@Data
public class GroupBlacklistRequestDto {
    private int userId;
    private String reason;
}
