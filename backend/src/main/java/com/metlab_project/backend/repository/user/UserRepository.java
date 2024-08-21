package com.metlab_project.backend.repository.user;

import com.metlab_project.backend.domain.entity.BlacklistToken;
import com.metlab_project.backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findBySchoolEmail(String schoolEmail);
}
