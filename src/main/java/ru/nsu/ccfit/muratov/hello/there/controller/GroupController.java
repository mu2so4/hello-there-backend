package ru.nsu.ccfit.muratov.hello.there.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ccfit.muratov.hello.there.dto.Group;

@RestController
@RequestMapping("/api/groups")
@Tag(name = "Group management")
public class GroupController {
    @Operation(
            summary = "Fetch group list",
            description = "Get all groups of the social network"
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Success",
                    responseCode = "200"
            ),
            @ApiResponse(
                    description = "Unauthorized",
                    responseCode = "401"
            )

    })
    @GetMapping
    public void getAllGroups() {
        //todo something
    }


    @Operation(
            summary = "Get a specific group",
            description = "Gets a group by group ID."
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Success",
                    responseCode = "200"
            ),
            @ApiResponse(
                    description = "Unauthorized",
                    responseCode = "401"
            ),
            @ApiResponse(
                    description = "Group not found",
                    responseCode = "404"
            )
    })
    @GetMapping("/{groupId}")
    public void getGroupById(@PathVariable String groupId) {
        System.out.println(groupId);
    }


    @Operation(
            description = "Creates a new group",
            summary = "Create a new group"
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Success",
                    responseCode = "201"
            ),
            @ApiResponse(
                    description = "Invalid group parameters or some of them not set",
                    responseCode = "400"
            ),
            @ApiResponse(
                    description = "Unauthorized",
                    responseCode = "401"
            )
    })
    @PostMapping
    public Group createGroup(@RequestBody Group params) {
        return params;
    }


    @Operation(
            summary = "Update group data",
            description = "Updates some group params such as description by group ID."
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Success",
                    responseCode = "200"
            ),
            @ApiResponse(
                    description = "No valid parameters set",
                    responseCode = "400"
            ),
            @ApiResponse(
                    description = "Unauthorized",
                    responseCode = "401"
            ),
            @ApiResponse(
                    description = "User has no rights to edit this group",
                    responseCode = "403"
            ),
            @ApiResponse(
                    description = "Group not found",
                    responseCode = "404"
            )
    })
    @PatchMapping("/{groupId}")
    public Group updateGroup(@PathVariable("groupId") String groupId, @RequestBody Group newParams) {
        return newParams;
    }

    @Operation(
            summary = "Delete group",
            description = "Deletes group by its group ID."
    )
    @ApiResponses({
            @ApiResponse(
                    description = "Success",
                    responseCode = "204"
            ),
            @ApiResponse(
                    description = "Unauthorized",
                    responseCode = "401"
            ),
            @ApiResponse(
                    description = "User has no rights to delete this group",
                    responseCode = "403"
            ),
            @ApiResponse(
                    description = "Group not found or already deleted",
                    responseCode = "404"
            )
    })
    @DeleteMapping("/{groupId}")
    public void updateGroup(@PathVariable("groupId") String groupId) {

    }
}
