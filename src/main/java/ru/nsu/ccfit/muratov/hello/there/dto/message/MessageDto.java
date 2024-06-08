package ru.nsu.ccfit.muratov.hello.there.dto.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.entity.Message;

import java.util.Date;

@Data
public class MessageDto {
    private int messageId;
    private UserDto sender;
    private UserDto receiver;
    private String content;
    private Date sendTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date lastEditTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RepliedMessageDto repliedMessage;

    public MessageDto(Message message) {
        this.messageId = message.getId();
        this.sender = new UserDto(message.getSender());
        this.receiver = new UserDto(message.getReceiver());
        this.content = message.getContent();
        this.sendTime = message.getSendTime();
        this.lastEditTime = message.getLastEditTime();
        if(message.getRepliedMessage() != null) {
            repliedMessage = new RepliedMessageDto(message.getRepliedMessage());
        }
    }


    @Data
    public static class UserDto {
        private int userId;
        private String firstName;
        private String lastName;

        public UserDto(UserEntity user) {
            this.userId = user.getId();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
        }
    }

    @Data
    public static class RepliedMessageDto {
        private int messageId;
        private String content;
        private UserDto sender;

        public RepliedMessageDto(Message message) {
            this.messageId = message.getId();
            this.content = message.getContent();
            this.sender = new UserDto(message.getSender());
        }
    }
}
