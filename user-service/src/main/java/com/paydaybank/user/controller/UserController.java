package com.paydaybank.user.controller;

import com.paydaybank.user.dto.UserDTO;
import com.paydaybank.user.service.UserService;

import lombok.RequiredArgsConstructor;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id,
                                               @RequestHeader("X-User-Id") UUID authenticatedUserId) {
        if (!id.equals(authenticatedUserId)) {
            throw new RuntimeException("Access denied: You can only view your own profile");
        }
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
