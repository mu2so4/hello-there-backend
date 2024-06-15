package ru.nsu.ccfit.muratov.hello.there.dto.message;

import lombok.Data;

@Data
public class MessageRequestDto {
    private int receiverId;
    private String content;
    private Integer repliedMessageId;
}
