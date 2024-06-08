package ru.nsu.ccfit.muratov.hello.there.dto;

import lombok.Data;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;

@Data
public class UserDto {
    private int userId;
    private String firstName;
    private String lastName;

    public UserDto(UserEntity user) {
        this.userId = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }
}
