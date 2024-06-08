package ru.nsu.ccfit.muratov.hello.there.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.nsu.ccfit.muratov.hello.there.dto.blacklist.user.UserBlacklistRequestDto;
import ru.nsu.ccfit.muratov.hello.there.dto.blacklist.user.UserBlacklistResponseDto;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.repository.UserBlacklistRepository;
import ru.nsu.ccfit.muratov.hello.there.repository.UserRepository;
import ru.nsu.ccfit.muratov.hello.there.service.UserEntityService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/profile/blacklist", produces = "application/json")
@Tag(name = "User blacklist")
public class UserBlacklistController {
    @Value("${data.user.blacklist.page.size}")
    private int pageSize;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserBlacklistRepository userBlacklistRepository;
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
    public List<UserBlacklistResponseDto> getBlacklist(@RequestParam(defaultValue = "1", name = "page") int pageNumber, @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity blocker = userEntityService.getUserByUserDetails(userDetails);
        int internalPageNumber = pageNumber - 1;
        Pageable pageable = PageRequest.of(internalPageNumber, pageSize, Sort.by("id"));
        return userBlacklistRepository.findByBlocker(blocker, pageable).stream()
                .map((blacklistRecord) -> new UserBlacklistResponseDto(blacklistRecord.getBlocker(), blacklistRecord.getBlocked()))
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
    public UserBlacklistResponseDto addToBlacklist(@RequestBody UserBlacklistRequestDto dto, @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity blocker = userEntityService.getUserByUserDetails(userDetails);
        int blockedId = dto.getId();
        if(blocker.getId() == blockedId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attempt to add themselves to blacklist");
        }
        UserEntity blocked;
        try {
            blocked = userRepository.getReferenceById(blockedId);
            blocker.getBlacklist().add(blocked);
        }
        catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.save(blocker);
        return new UserBlacklistResponseDto(blocker, blocked);
    }

    @Operation(
            summary = "Remove user from user's blacklist",
            description = "Removes user from user's blacklist by their ID."
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
                    description = "User to be unblacklisted not found",
                    content = @Content
            )
    })
    @DeleteMapping("/{userId}")
    public void removeFromBlacklist(@PathVariable int userId, @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity blocker = userEntityService.getUserByUserDetails(userDetails);
        try {
            UserEntity blocked = userRepository.getReferenceById(userId);
            blocker.getBlacklist().remove(blocked);
        }
        catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.save(blocker);
    }
}
