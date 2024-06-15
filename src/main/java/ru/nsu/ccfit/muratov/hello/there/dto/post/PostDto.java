package ru.nsu.ccfit.muratov.hello.there.dto.post;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.nsu.ccfit.muratov.hello.there.dto.SubscriptionDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Post;

import java.util.Date;

@Data
public class PostDto {
    private Integer postId;
    private String content;
    private Date createTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date lastEditTime;
    private SubscriptionDto.GroupDto group;

    public PostDto(Post post) {
        this.postId = post.getId();
        this.content = post.getContent();
        this.createTime = post.getCreateTime();
        this.lastEditTime = post.getLastEditTime();
        this.group = new SubscriptionDto.GroupDto(post.getGroup());
    }
}
