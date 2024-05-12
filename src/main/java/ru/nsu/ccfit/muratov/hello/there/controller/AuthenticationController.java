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
@RequestMapping("/api/auth")
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
        //if(userRepository.ex)
        UserEntity user = UserEntity.builder()
                .username(form.getUsername())
                .password(passwordEncoder.encode(form.getPassword()))
                .firstName(form.getFirstName())
                .lastName(form.getLastName())
                .registrationTime(new Date())
                .birthday(form.getBirthday())
                .build();
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
            )/*,
            @ApiResponse(
                    description = "Invalid credentials",
                    responseCode = "401"
            )*/
    })
    @PostMapping("/login")
    @ResponseStatus(code = HttpStatus.CREATED)
    public RegistrationResponseDto login(@RequestBody LoginDto dto) {
        String username = dto.getUsername();
        logger.info(() -> String.format("received login request for user \"%s\"", username));
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserEntity user;
        user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("username not found"));
        return RegistrationResponseDto.createResponse(user);
    }

    @DeleteMapping("/login")
    public void logout() {
    }
}
