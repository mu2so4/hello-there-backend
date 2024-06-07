package ru.nsu.ccfit.muratov.hello.there.dto.message;

import lombok.Data;
import ru.nsu.ccfit.muratov.hello.there.entity.message.Message;
import ru.nsu.ccfit.muratov.hello.there.entity.message.PrivateMessage;

import java.util.Date;

@Data
public class PrivateMessageResponseDto {
    private int messageId;
    private int senderId;
    private int receiverId;
    private String content;
    private Date sendTime;
    private Integer repliedMessageId;

    public PrivateMessageResponseDto(PrivateMessage privateMessage) {
        Message message = privateMessage.getMessage();
        this.messageId = message.getId();
        this.senderId = message.getSender().getId();
        this.receiverId = privateMessage.getReceiver().getId();
        this.content = message.getContent();
        this.sendTime = message.getSendTime();

        Message repliedMessage = message.getRepliedMessage();
        if(repliedMessage != null) {
            this.repliedMessageId = repliedMessage.getId();
        }
    }
}
