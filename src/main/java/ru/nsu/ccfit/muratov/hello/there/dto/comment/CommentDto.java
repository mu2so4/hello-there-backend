package ru.nsu.ccfit.muratov.hello.there.dto.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.nsu.ccfit.muratov.hello.there.dto.UserDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Comment;

import java.util.Date;

@Data
public class CommentDto {
    private int commentId;
    private String content;
    private Date createTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date lastEditTime;
    private UserDto commenter;

    public CommentDto(Comment comment) {
        commentId = comment.getId();
        content = comment.getContent();
        createTime = comment.getCreateTime();
        lastEditTime = comment.getLastEditTime();
        commenter = new UserDto(comment.getCommenter());
    }
}
