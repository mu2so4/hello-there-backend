package ru.nsu.ccfit.muratov.hello.there.dto.post;

import lombok.Data;

@Data
public class PostRequestDto {
    private Integer groupId;
    private String content;
}
