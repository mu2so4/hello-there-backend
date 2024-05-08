package ru.nsu.ccfit.muratov.hello.there.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RegistrationRequestDto {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private Date birthday;
}
