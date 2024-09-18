package com.metlab_project.backend.repository.email;

import com.metlab_project.backend.domain.entity.email.*;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {
    Optional<EmailAuth> findEmailAuthByEmail(String email);
    Optional<EmailAuth> findEmailAuthByKey(String key);
    boolean existsByEmail(String email);
    boolean existsByKey(String key);
    void deleteByEmail(String email);
}
