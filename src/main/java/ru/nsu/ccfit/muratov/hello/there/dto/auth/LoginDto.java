package ru.nsu.ccfit.muratov.hello.there.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginDto {
    @Schema(example = "user")
    private String username;
    @Schema(example = "1234")
    private String password;
}
