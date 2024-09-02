package com.metlab_project.backend.repository.user;

import com.metlab_project.backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findBySchoolEmail(String schoolEmail);
}
