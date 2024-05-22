package ru.nsu.ccfit.muratov.hello.there.dto;

import lombok.Data;
import ru.nsu.ccfit.muratov.hello.there.entity.Group;

import java.util.Date;

@Data
public class GroupDto {
    private int id;
    private String name;
    private String description;
    private Date createTime;
    private int ownerId;

    public GroupDto(Group group) {
        this.id = group.getId();
        this.name = group.getName();
        this.description = group.getDescription();
        this.createTime = group.getCreateTime();
        this.ownerId = group.getOwner().getId();
    }
}
