package com.metlab_project.backend.repository.jwt;

import com.metlab_project.backend.domain.entity.jwt.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshEntity, Long> {
    Optional<RefreshEntity> findByUser_SchoolEmail(String schoolEmail);
    Boolean existsByToken(String token);
    Optional<RefreshEntity> findByToken(String token);
}
