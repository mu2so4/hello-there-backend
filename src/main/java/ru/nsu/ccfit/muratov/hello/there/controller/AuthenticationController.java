package ru.nsu.ccfit.muratov.hello.there.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.nsu.ccfit.muratov.hello.there.exception.BadRequestException;
import ru.nsu.ccfit.muratov.hello.there.security.JwtAuthFilter;
import ru.nsu.ccfit.muratov.hello.there.security.TokenBlacklist;
import ru.nsu.ccfit.muratov.hello.there.service.JwtService;
import ru.nsu.ccfit.muratov.hello.there.dto.auth.AuthResponseDto;
import ru.nsu.ccfit.muratov.hello.there.dto.auth.LoginDto;
import ru.nsu.ccfit.muratov.hello.there.dto.auth.RegistrationRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.auth.RegistrationResponseDto;
import ru.nsu.ccfit.muratov.hello.there.service.UserEntityService;

import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/api/auth")
@Tag(name = "Authentication")
public class AuthenticationController {
    private final JwtService jwtService;
    private final TokenBlacklist tokenBlacklist;
    private final UserEntityService userEntityService;

    private static final Logger logger = Logger.getLogger(AuthenticationController.class.getCanonicalName());

    public AuthenticationController(JwtService jwtService, TokenBlacklist tokenBlacklist, UserEntityService userEntityService) {
        this.jwtService = jwtService;
        this.tokenBlacklist = tokenBlacklist;
        this.userEntityService = userEntityService;
    }

    @Operation(summary = "Register a new user",
            description = "Registers a new user in the system."
    )
    @ApiResponses({
            @ApiResponse(description = "Success", responseCode = "201"),
            @ApiResponse(description = "Invalid form", responseCode = "400", content = @Content)
    })
    @PostMapping(value = "/register", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponseDto registerNewUser(@RequestBody RegistrationRequestDto form) throws BadRequestException {
        return new RegistrationResponseDto(userEntityService.registerUser(form));
    }

    @Operation(summary = "Sign in to the system")
    @ApiResponses({
            @ApiResponse(description = "Success", responseCode = "201"),
            @ApiResponse(description = "Invalid credentials", responseCode = "400", content = @Content)
    })
    @PostMapping(value = "/login", produces = "application/json")
    @ResponseStatus(code = HttpStatus.CREATED)
    public AuthResponseDto login(@RequestBody LoginDto dto) {
        String username = dto.getUsername();
        logger.info(() -> String.format("received login request for user \"%s\"", username));
        try {
            String token = jwtService.login(dto);
            return new AuthResponseDto(token);
        }
        catch(AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Logout user",
            description = "Invalidates their JWT"
    )
    @ApiResponses({
            @ApiResponse(description = "Success", responseCode = "204"),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content)
    })
    @DeleteMapping(value = "/logout")
    public void logout(HttpServletRequest request) {
        String token = JwtAuthFilter.getJwtFromRequest(request);
        tokenBlacklist.addToBlacklist(token);
    }
}
