package ru.nsu.ccfit.muratov.hello.there.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.nsu.ccfit.muratov.hello.there.dto.LoginDto;
import ru.nsu.ccfit.muratov.hello.there.dto.RegistrationRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.RegistrationResponseDto;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.repository.UserRepository;

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
    private UserRepository userRepository;

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
                    responseCode = "400"
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
                    responseCode = "400"
            )
    })
    @PostMapping("/login")
    @ResponseStatus(code = HttpStatus.CREATED)
    public RegistrationResponseDto login(@RequestBody LoginDto dto) {
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
        UserEntity user;
        user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("username not found"));
        return RegistrationResponseDto.createResponse(user);
    }

    @DeleteMapping("/login")
    public void logout() {
    }
}
