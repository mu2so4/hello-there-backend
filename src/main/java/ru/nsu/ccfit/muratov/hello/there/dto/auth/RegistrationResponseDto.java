package ru.nsu.ccfit.muratov.hello.there.dto.auth;

import lombok.Builder;
import lombok.Data;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;

import java.util.Date;

@Data
@Builder
public class RegistrationResponseDto {
    private int userId;
    private String username;
    private String firstName;
    private String lastName;
    private Date registrationTime;
    private Date birthday;

    public static RegistrationResponseDto createResponse(UserEntity user) {
        return RegistrationResponseDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .registrationTime(user.getRegistrationTime())
                .birthday(user.getBirthday())
                .build();
    }
}
