package ru.nsu.ccfit.muratov.hello.there.dto;

import lombok.Data;

import java.util.Date;

@Data
public class Group {
    private int groupId;
    private String groupName;
    private String description;
    private Date createTime;
    private User owner;
}
