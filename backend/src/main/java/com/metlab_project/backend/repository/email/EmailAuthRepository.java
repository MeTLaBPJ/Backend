package com.metlab_project.backend.repository.email;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.metlab_project.backend.domain.entity.email.EmailAuth;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, String> {
    Optional<EmailAuth> findEmailAuthByEmail(String email);
    Optional<EmailAuth> findEmailAuthByKey(String key);
    boolean existsByEmail(String email);
    boolean existsByKey(String key);
    void deleteByEmail(String email);
}