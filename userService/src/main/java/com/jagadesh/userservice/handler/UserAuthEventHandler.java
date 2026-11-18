package com.jagadesh.userservice.handler;

import com.jagadesh.event.user.UserCreatedEvent;
import com.jagadesh.userservice.model.User;
import com.jagadesh.userservice.model.UserPreference;
import com.jagadesh.userservice.repository.UserPreferenceRepo;
import com.jagadesh.userservice.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@KafkaListener(
        topics = "user-event",
        groupId = "user-group"
)
@Service
@Slf4j
public class UserAuthEventHandler {

    private UserRepo userRepo;
    private UserPreferenceRepo userPreferenceRepo;

    public UserAuthEventHandler(UserRepo userRepo,UserPreferenceRepo userPreferenceRepo) {
        this.userRepo = userRepo;
        this.userPreferenceRepo = userPreferenceRepo;
    }

    @KafkaHandler
    @Transactional
    public void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        User newUser = new User(
                userCreatedEvent.getUserId(),
                userCreatedEvent.getEmailId(),
                userCreatedEvent.getUserName()
        );
        UserPreference newUserPreference = new UserPreference(
                userCreatedEvent.getUserId()
        );
        userRepo.save(newUser);
        userPreferenceRepo.save(newUserPreference);

        log.info("EventId={} UserId={} action=USER_CREATED status=SUCCESS",
                userCreatedEvent.getEventId(),
                userCreatedEvent.getUserId()
        );
    }

    @KafkaHandler(isDefault = true)
    public void handleDefaultEvent(Object event) {
        log.warn("Unhandled event received: {}", event);
    }
}
