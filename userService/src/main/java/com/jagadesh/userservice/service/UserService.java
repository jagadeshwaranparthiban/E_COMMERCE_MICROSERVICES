package com.jagadesh.userservice.service;

import com.jagadesh.userservice.dto.UserResponseDto;
import com.jagadesh.userservice.exception.UserNotFoundException;
import com.jagadesh.userservice.model.User;
import com.jagadesh.userservice.repository.UserRepo;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public List<UserResponseDto> getAllUsers() {
        List<UserResponseDto> users = userRepo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return users;
    }

    public UserResponseDto getUserById(String id) {
        Optional<User> existingUser = userRepo.findById(id);

        if(existingUser.isEmpty()) {
            throw new UserNotFoundException("User with id " + id + " does not exist");
        }
        User user = existingUser.get();

        return toDto(user);
    }

    public UserResponseDto getUserByUsername(String username) {
        Optional<User> existingUser = userRepo.findByUserName(username);

        if(existingUser.isEmpty()) {
            throw new UserNotFoundException("User with username " + username + " does not exist");
        }
        User user = existingUser.get();

        return toDto(user);
    }
    
    public void updateUser() {}

    private UserResponseDto toDto(User user) {
        return new UserResponseDto(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getStatus().toString(),
                user.isVerified(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
