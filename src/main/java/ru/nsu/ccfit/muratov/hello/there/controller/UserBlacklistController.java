package ru.nsu.ccfit.muratov.hello.there.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ccfit.muratov.hello.there.dto.UserDto;
import ru.nsu.ccfit.muratov.hello.there.dto.blacklist.user.UserBlacklistRequestDto;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.BadRequestException;
import ru.nsu.ccfit.muratov.hello.there.exception.UserNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.service.UserEntityService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/profile/blacklist", produces = "application/json")
@Tag(name = "User blacklist")
public class UserBlacklistController {
    @Value("${data.user.blacklist.page.size}")
    private int pageSize;
    @Autowired
    private UserEntityService userEntityService;

    @Operation(
            summary = "Retrieve user's blacklist",
            description = "Retrieves user's blacklist. Blacklist is ordered by user IDs."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            )
    })
    @GetMapping
    public List<UserDto> getBlacklist(@RequestParam(defaultValue = "0", name = "page") int pageNumber, @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity blocker = userEntityService.getUserByUserDetails(userDetails);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id"));
        return userEntityService.getBlacklist(blocker, pageable).stream()
                .map(record -> new UserDto(record.getBlocked()))
                .toList();
    }

    @Operation(
            summary = "Add user to user's blacklist",
            description = "Adds user to user's blacklist by their ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Success"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User to be blacklisted not found",
                    content = @Content
            )
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserDto addToBlacklist(@RequestBody UserBlacklistRequestDto dto, @AuthenticationPrincipal UserDetails userDetails)
            throws UserNotFoundException, BadRequestException {
        UserEntity blocker = userEntityService.getUserByUserDetails(userDetails);
        UserEntity blocked = userEntityService.getById(dto.getId());
        userEntityService.addToBlacklist(blocker, blocked);
        return new UserDto(blocked);
    }

    @Operation(
            summary = "Remove user from user's blacklist",
            description = "Removes user from user's blacklist by their ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Success"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User to be unblacklisted not found",
                    content = @Content
            )
    })
    @DeleteMapping("/{userId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void removeFromBlacklist(@PathVariable int userId, @AuthenticationPrincipal UserDetails userDetails) throws UserNotFoundException {
        UserEntity blocker = userEntityService.getUserByUserDetails(userDetails);
        UserEntity blocked = userEntityService.getById(userId);
        userEntityService.removeFromBlacklist(blocker, blocked);
    }
}
