package ru.nsu.ccfit.muratov.hello.there.dto.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.nsu.ccfit.muratov.hello.there.entity.Message;

import java.util.Date;

@Data
public class MessageResponseDto {
    private int messageId;
    private int senderId;
    private int receiverId;
    private String content;
    private Date sendTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer repliedMessageId;

    public MessageResponseDto(Message message) {
        this.messageId = message.getId();
        this.senderId = message.getSender().getId();
        this.receiverId = message.getReceiver().getId();
        this.content = message.getContent();
        this.sendTime = message.getSendTime();

        Message repliedMessage = message.getRepliedMessage();
        if(repliedMessage != null) {
            this.repliedMessageId = repliedMessage.getId();
        }
    }
}
