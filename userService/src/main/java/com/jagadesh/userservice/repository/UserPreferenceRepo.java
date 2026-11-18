package com.jagadesh.userservice.repository;

import com.jagadesh.userservice.model.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface UserPreferenceRepo extends JpaRepository<UserPreference, String> {
}
