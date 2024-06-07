package ru.nsu.ccfit.muratov.hello.there.dto.message;

import lombok.Data;

@Data
public class PrivateMessageRequestDto {
    private String content;
    private Integer repliedMessageId;
}
