package com.metlab_project.backend.repository.jwt;

import com.metlab_project.backend.domain.entity.jwt.BlacklistToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistTokenRepository extends JpaRepository<BlacklistToken, Integer> {
    boolean existsByToken(String token);
}
