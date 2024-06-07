package ru.nsu.ccfit.muratov.hello.there.dto.message;

import lombok.Data;
import ru.nsu.ccfit.muratov.hello.there.entity.message.Message;

import java.util.Date;

@Data
public class MessageUpdateResponseDto {
    private int messageId;
    private int senderId;
    private String content;
    private Date sendTime;
    private Date lastEditTime;
    private Integer repliedMessageId;

    public MessageUpdateResponseDto(Message message) {
        this.messageId = message.getId();
        this.senderId = message.getSender().getId();
        this.content = message.getContent();
        this.sendTime = message.getSendTime();
        this.lastEditTime = message.getLastEditTime();
        Message repliedMessage = message.getRepliedMessage();
        if(repliedMessage != null) {
            this.repliedMessageId = repliedMessage.getId();
        }
    }
}
