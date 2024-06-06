package ru.nsu.ccfit.muratov.hello.there.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.nsu.ccfit.muratov.hello.there.service.JwtService;
import ru.nsu.ccfit.muratov.hello.there.dto.auth.AuthResponseDto;
import ru.nsu.ccfit.muratov.hello.there.dto.auth.LoginDto;
import ru.nsu.ccfit.muratov.hello.there.dto.auth.RegistrationRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.auth.RegistrationResponseDto;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.repository.RoleRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.UserRepository;

import java.util.Collections;
import java.util.Date;
import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/api/auth", produces = "application/json")
@Tag(name = "Authentication")
public class AuthenticationController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    private static final String USER_ROLE_NAME = "USER";

    private static final Logger logger = Logger.getLogger(AuthenticationController.class.getCanonicalName());

    @Operation(
            summary = "Register a new user",
            description = "Registers a new user in the system."
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Success",
                    responseCode = "201"
            ),
            @ApiResponse(
                    description = "Invalid form",
                    responseCode = "400",
                    content = @Content
            )
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponseDto registerNewUser(@RequestBody RegistrationRequestDto form) {
        UserEntity user = new UserEntity();
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        user.setRegistrationTime(new Date());
        user.setBirthday(form.getBirthday());
        user.setRoles(Collections.singleton(roleRepository.findByName(USER_ROLE_NAME)));
        UserEntity savedUser = userRepository.save(user);
        return RegistrationResponseDto.createResponse(savedUser);
    }

    @Operation(
            summary = "Sign in to the system"
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Success",
                    responseCode = "201"
            ),
            @ApiResponse(
                    description = "Invalid credentials",
                    responseCode = "401",
                    content = @Content
            )
    })
    @PostMapping("/login")
    @ResponseStatus(code = HttpStatus.CREATED)
    public AuthResponseDto login(@RequestBody LoginDto dto) {
        String username = dto.getUsername();
        logger.info(() -> String.format("received login request for user \"%s\"", username));
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, dto.getPassword()));
        }
        catch(AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken(authentication);
        return new AuthResponseDto(token);
    }

    @DeleteMapping("/login")
    public void logout() {
    }
}
