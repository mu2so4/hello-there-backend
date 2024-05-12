package ru.nsu.ccfit.muratov.hello.there.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class RegistrationRequestDto {
    @Schema(description = "username the user desires to use. Must be unique.", example = "username")
    private String username;
    @Schema(example = "1234")
    private String password;
    @Schema(description = "User's first name", example = "John")
    private String firstName;
    @Schema(description = "User's last name", example = "Smith")
    private String lastName;
    @Schema(description = "User's birthday", example = "1970-01-01")
    private Date birthday;
}
