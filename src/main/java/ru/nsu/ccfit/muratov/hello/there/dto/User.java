package ru.nsu.ccfit.muratov.hello.there.dto;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private int userId;
    private String nickname;
    private String firstName;
    private String lastName;
    private Date registrationTime;
    private Date birthday;
}
