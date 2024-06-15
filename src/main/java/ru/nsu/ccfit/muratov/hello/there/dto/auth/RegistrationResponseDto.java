package ru.nsu.ccfit.muratov.hello.there.dto.auth;

import lombok.Data;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;

import java.util.Date;

@Data
public class RegistrationResponseDto {
    private int userId;
    private String username;
    private String firstName;
    private String lastName;
    private Date registrationTime;
    private Date birthday;

    public RegistrationResponseDto(UserEntity user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.registrationTime = user.getRegistrationTime();
        this.birthday = user.getBirthday();
    }
}
