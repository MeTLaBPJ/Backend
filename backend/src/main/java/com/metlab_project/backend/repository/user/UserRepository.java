package com.metlab_project.backend.repository.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.metlab_project.backend.domain.entity.user.User;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Boolean existsBySchoolEmail(String schoolEmail);

    Optional<User> findBySchoolEmail(String schoolEmail);

    Page<User> findAll(Pageable pageable);

    Boolean existsByNickname(String nickname);

    Optional<User> findByNickname(String nickname);
}

