package com.metlab_project.backend.service.user;

import com.metlab_project.backend.domain.dto.user.UserInfoResponse;
import com.metlab_project.backend.domain.entity.User;
import com.metlab_project.backend.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserInfoResponse getUserInfoBySchoolEmail(String schoolEmail){
        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid Email"));

        return UserInfoResponse.builder()
                .schoolEmail(user.getSchoolEmail())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .studentId(user.getStudentId())
                .college(user.getCollege())
                .department(user.getDepartment())
                .build();
    }

    public String getRefreshToken(String schoolEmail){
        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid Email"));

        return user.getRefreshtoken();
    }
}
