package ru.nsu.ccfit.muratov.hello.there.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ccfit.muratov.hello.there.dto.LoginDto;
import ru.nsu.ccfit.muratov.hello.there.dto.RegistrationRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.RegistrationResponseDto;
import ru.nsu.ccfit.muratov.hello.there.entity.User;
import ru.nsu.ccfit.muratov.hello.there.repository.UserRepository;

import java.util.Date;

@RestController
@RequestMapping("/api")
@Tag(name = "Authentication")
public class AuthenticationController {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

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
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponseDto registerNewUser(@RequestBody RegistrationRequestDto form) {
        //if(userRepository.ex)
        User user = User.builder()
                .username(form.getUsername())
                .password(passwordEncoder.encode(form.getPassword()))
                .firstName(form.getFirstName())
                .lastName(form.getLastName())
                .registrationTime(new Date())
                .birthday(form.getBirthday())
                .build();
        User savedUser = userRepository.save(user);
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
                    description = "",
                    responseCode = "400"
            ),
            @ApiResponse(
                    description = "Invalid credentials",
                    responseCode = "401"
            )
    })
    @PostMapping("/session")
    public void signIn(@RequestBody LoginDto dto) {

    }

    @DeleteMapping("/session")
    public void signOut() {

    }
}
