package com.github.bael.otus.java.userservice.rest.controller;


import com.github.bael.otus.java.userservice.domain.UserAccountService;
import com.github.bael.otus.java.userservice.entity.UserAccount;
import com.github.bael.otus.java.userservice.rest.dto.UserAccountCreateRequest;
import com.github.bael.otus.java.userservice.rest.dto.UserAccountResponse;
import com.github.bael.otus.java.userservice.rest.dto.UserAccountUpdateRequest;
import com.github.bael.otus.java.userservice.rest.mapper.UserAccountMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
        import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
public class UserAccountController {

    private final UserAccountService userService;
    private final UserAccountMapper userMapper;

    @Operation(summary = "Get all users", description = "Retrieve a list of all registered users")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved users",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserAccountResponse.class)))
    @GetMapping
    public ResponseEntity<List<UserAccountResponse>> getAllUsers() {
        List<UserAccount> users = userService.findAll();
        return ResponseEntity.ok(userMapper.toResponseList(users));
    }

    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserAccountResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserAccountResponse> getUserById(
            @Parameter(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {

        return userService.findById(id)
                .map(user -> ResponseEntity.ok(userMapper.toResponse(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new user", description = "Register a new user in the system")
    @ApiResponse(responseCode = "201", description = "User created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserAccountResponse.class)))
    @PostMapping
    public ResponseEntity<UserAccountResponse> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User creation data")
            @Valid @RequestBody UserAccountCreateRequest request) {

        UserAccount user = userMapper.toEntity(request);
        // Здесь должна быть логика хеширования пароля
        user.setPasswordHash(request.getPassword());
        // todo
        UserAccount savedUser = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.toResponse(savedUser));
    }

    @Operation(summary = "Update user", description = "Update an existing user's information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserAccountResponse> updateUser(
            @Parameter(description = "User ID") @PathVariable UUID id,
            @Valid @RequestBody UserAccountUpdateRequest request) {

        return userService.findById(id)
                .map(user -> {
                    userMapper.updateEntityFromRequest(request, user);
                    UserAccount updatedUser = userService.save(user);
                    return ResponseEntity.ok(userMapper.toResponse(updatedUser));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete user", description = "Remove a user from the system")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID") @PathVariable UUID id) {

        if (userService.existsById(id)) {
            userService.disableById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get user by username", description = "Find a user by their username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/username/{username}")
    public ResponseEntity<UserAccountResponse> getUserByUsername(
            @Parameter(description = "Username") @PathVariable String username) {

        return userService.findByUsername(username)
                .map(user -> ResponseEntity.ok(userMapper.toResponse(user)))
                .orElse(ResponseEntity.notFound().build());
    }
}