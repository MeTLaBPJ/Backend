package com.metlab_project.backend.repository.user;

import com.metlab_project.backend.domain.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshEntity, Long> {
    Optional<RefreshEntity> findBySchoolEmail(String schoolEmail); // schoolEmail로 조회
    Boolean existsByToken(String token);
    Optional<RefreshEntity> findByToken(String token);
}
