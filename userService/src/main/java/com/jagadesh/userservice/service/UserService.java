package com.jagadesh.userservice.service;

import com.jagadesh.userservice.dto.UserResponseDto;
import com.jagadesh.userservice.exception.AuthorizationViolationException;
import com.jagadesh.userservice.exception.EmailAlreadyExistsException;
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

    public UserResponseDto getUserById(String id, String requesterUsername, Boolean isAdmin) {
        Optional<User> existingUser = userRepo.findById(id);

        if(existingUser.isEmpty()) {
            throw new UserNotFoundException("User with id " + id + " does not exist");
        }
        User user = existingUser.get();

        if(isAdmin) {
            return toDto(user);
        }

        if(!user.getUserName().equals(requesterUsername)) {
            throw new AuthorizationViolationException("You are not authorized to access this user's information");
        }

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

    public UserResponseDto updateUserProfile(String userName, String newEmail) {
        User existingUser = userRepo.findByUserName(userName)
                .orElseThrow(() -> new UserNotFoundException("User with username " + userName + " does not exist"));

        if(userRepo.existsByEmailIgnoreCaseAndUserIdNot(newEmail, existingUser.getUserId())) {
            throw new EmailAlreadyExistsException("Email " + newEmail + " is already in use by another user");
        }

        existingUser.updateEmail(newEmail);
        User updatedUser = userRepo.save(existingUser);

        return toDto(updatedUser);
    }

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
