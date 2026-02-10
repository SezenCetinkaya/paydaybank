package com.paydaybank.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import java.util.UUID;

@FeignClient(name = "user-service", url = "${application.config.user-service-url}")
public interface UserClient {

    @PostMapping("/internal/users")
    UserDTO createUser(@RequestBody UserDTO userDTO);

    @org.springframework.web.bind.annotation.DeleteMapping("/internal/users/{id}")
    void deleteUser(@org.springframework.web.bind.annotation.PathVariable("id") java.util.UUID id);

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    class UserDTO {
        private UUID id;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private String gender;
        private LocalDate dateOfBirth;
 
    }
}
