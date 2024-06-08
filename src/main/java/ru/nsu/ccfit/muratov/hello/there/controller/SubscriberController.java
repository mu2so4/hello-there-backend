package ru.nsu.ccfit.muratov.hello.there.controller;

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
import org.springframework.web.server.ResponseStatusException;
import ru.nsu.ccfit.muratov.hello.there.dto.SubscriptionDto;
import ru.nsu.ccfit.muratov.hello.there.entity.Group;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.exception.BadRequestException;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupBlacklistedException;
import ru.nsu.ccfit.muratov.hello.there.exception.GroupNotFoundException;
import ru.nsu.ccfit.muratov.hello.there.service.GroupService;
import ru.nsu.ccfit.muratov.hello.there.service.UserEntityService;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/subscribers")
@Tag(name = "Group subscribers")
public class SubscriberController {
    @Autowired
    private UserEntityService userEntityService;
    @Autowired
    private GroupService groupService;

    @Value("${data.groups.subscribers.page.size}")
    private int pageSize;

    @GetMapping(produces = "application/json")
    public List<SubscriptionDto> getSubscribers(@PathVariable int groupId,
                                                @RequestParam(name = "page", defaultValue = "0") int pageNumber,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity user = userEntityService.getUserByUserDetails(userDetails);
        try {
            Group group = groupService.getById(groupId);
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("subscriptionTime"));
            return groupService.getSubscriberList(group, user, pageable).stream()
                    .map(SubscriptionDto::new)
                    .toList();
        }
        catch(GroupNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch(GroupBlacklistedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }


    @PostMapping(produces = "application/json")
    @ResponseStatus(code = HttpStatus.CREATED)
    public SubscriptionDto subscribe(@PathVariable int groupId,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity user = userEntityService.getUserByUserDetails(userDetails);
        try {
            Group group = groupService.getById(groupId);
            return new SubscriptionDto(groupService.subscribe(group, user));
        }
        catch(GroupNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch(GroupBlacklistedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @DeleteMapping
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable int groupId,
                            @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity user = userEntityService.getUserByUserDetails(userDetails);
        try {
            Group group = groupService.getById(groupId);
            groupService.unsubscribe(group, user);
        }
        catch(GroupNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch(BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
