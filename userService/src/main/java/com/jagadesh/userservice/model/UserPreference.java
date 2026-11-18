package com.jagadesh.userservice.model;

import jakarta.persistence.*;

@Entity
@Table( name = "user_preferences")
public class UserPreference {
    @Id
    private String userId;

    @Column( name = "is_email_enabled")
    private Boolean emailEnabled;

    @Column( name = "is_push_enabled")
    private Boolean pushEnabled;

    protected UserPreference() {}

    public UserPreference(String userId) {
        this.userId = userId;
        this.emailEnabled = true;
        this.pushEnabled = true;
    }

    public void enableEmail() {
        this.emailEnabled = true;
    }

    public void disableEmail() {
        this.emailEnabled = false;
    }

    public void enableSms() {
        this.pushEnabled = true;
    }

    public void disableSms() {
        this.pushEnabled = false;
    }

    public String getUserId() {
        return userId;
    }

    public Boolean getEmailEnabled() {
        return emailEnabled;
    }

    public Boolean getPushEnabled() {
        return pushEnabled;
    }
}
