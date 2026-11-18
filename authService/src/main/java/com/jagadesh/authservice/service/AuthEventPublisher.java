package com.jagadesh.authservice.service;

import com.jagadesh.event.user.UserCreatedEvent;
import com.jagadesh.event.user.UserPasswordChangedEvent;
import com.jagadesh.event.user.UserPasswordResetRequestedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthEventPublisher {
    private KafkaTemplate<String, Object> kafkaTemplate;

    public AuthEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        kafkaTemplate.send(
                "user-event",
                userCreatedEvent.getUserId(),
                userCreatedEvent
        );
    }

    public void publishUserPasswordResetRequestedEvent(UserPasswordResetRequestedEvent userPasswordResetRequestedEvent) {
        kafkaTemplate.send(
                "user-event",
                userPasswordResetRequestedEvent.getUserId(),
                userPasswordResetRequestedEvent
        );
    }

    public void publishUserPasswordChangedEvent(UserPasswordChangedEvent userPasswordChangedEvent) {
        kafkaTemplate.send(
                "user-event",
                userPasswordChangedEvent.getUserId(),
                userPasswordChangedEvent
        );
    }
}
