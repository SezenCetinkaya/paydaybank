package com.paydaybank.auth.service;

import com.paydaybank.auth.dto.AuthDTO;
import com.paydaybank.auth.dto.LoginRequest;
import com.paydaybank.auth.entity.Authorization;
import com.paydaybank.auth.repository.AuthRepository;
import com.paydaybank.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private com.paydaybank.auth.client.UserClient userClient;

    public void signup(com.paydaybank.auth.dto.SignupRequest request) {
        // userDTO for user service
        com.paydaybank.auth.client.UserClient.UserDTO userDTO = com.paydaybank.auth.client.UserClient.UserDTO.builder()
                .email(request.getEmail())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .build();
        
        com.paydaybank.auth.client.UserClient.UserDTO createdUser = userClient.createUser(userDTO);
        
        // authDTO for auth service
        Authorization auth = new Authorization();
        auth.setUserId(createdUser.getId());
        auth.setEmail(createdUser.getEmail());
        auth.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        authRepository.save(auth);
    }

    public AuthDTO login(LoginRequest request) {
        Optional<Authorization> authOpt = authRepository.findByEmail(request.getUsername());
        
        if (authOpt.isPresent()) {
            Authorization auth = authOpt.get();
            if (passwordEncoder.matches(request.getPassword(), auth.getPasswordHash())) {
                auth.setLastLoginAt(ZonedDateTime.now());
                authRepository.save(auth);
                String token = jwtUtil.generateToken(auth.getEmail(), auth.getUserId());
                return AuthDTO.builder()
                        .token(token)
                        .userId(auth.getUserId())
                        .build();
            }
        }
        
        throw new RuntimeException("Invalid email or password");
    }
}
