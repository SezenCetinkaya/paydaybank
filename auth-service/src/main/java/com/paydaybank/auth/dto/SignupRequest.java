package com.paydaybank.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupRequest {
    private String email;

    @jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9]{6,}$", message = "Password must be at least 6 characters long and contain only alphanumeric characters.")
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String gender;
    private LocalDate dateOfBirth;
}
