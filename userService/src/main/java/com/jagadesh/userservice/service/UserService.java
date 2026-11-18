package com.jagadesh.userservice.service;

import com.jagadesh.userservice.dto.UserResponseDto;
import com.jagadesh.userservice.exception.UserNotFoundException;
import com.jagadesh.userservice.model.User;
import com.jagadesh.userservice.model.UserPreference;
import com.jagadesh.userservice.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
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
                .parallelStream()
                .map(user -> new UserResponseDto(
                        user.getUserId(),
                        user.getUserName(),
                        user.getEmail(),
                        user.getStatus().toString(),
                        user.isVerified(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()
                ))
                .collect(Collectors.toList());

        return users;
    }

    public UserResponseDto getUserById(String id) {
        Optional<User> existingUser = userRepo.findById(id);
        if(existingUser.isEmpty()) {
            throw new UserNotFoundException("User with id " + id + " does not exist");
        }
        User user = existingUser.get();
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

    public void updateUser() {}
}
