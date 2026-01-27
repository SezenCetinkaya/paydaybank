package com.paydaybank.user.service;

import com.paydaybank.user.dto.UserDTO;
import com.paydaybank.user.entity.User;
import com.paydaybank.user.mapper.UserMapper;
import com.paydaybank.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        log.info("Creating user with email: {}", userDTO.getEmail());

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Save user to db
        User user = userMapper.toEntity(userDTO);
        user = userRepository.save(user);

        // Publish UserCreatedEvent
        publishUserCreatedEvent(user);

        return userMapper.toDTO(user);
    }

    private void publishUserCreatedEvent(User user) {
        log.info("PUBLISHING EVENT: UserCreatedEvent for userId: {}", user.getId());
    }
    
    public UserDTO getUserById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

}
