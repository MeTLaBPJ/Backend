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

    public UserInfoResponse getUserInfoBySchoolEmail(String schoolEmail) {
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

    public String getRefreshToken(String schoolEmail) {
        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid Email"));

        return user.getRefreshtoken();
    }

    public UserInfoResponse updateUserInfo(String schoolEmail, UserInfoResponse userInfoResponse) {
        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // User 엔티티의 정보를 UserInfoResponse의 값으로 업데이트
        user.setNickname(userInfoResponse.getNickname());
        user.setGender(userInfoResponse.getGender());
        user.setStudentId(userInfoResponse.getStudentId());
        user.setCollege(userInfoResponse.getCollege());
        user.setDepartment(userInfoResponse.getDepartment());

        // 저장
        User updatedUser = userRepository.save(user);

        // UserInfoResponse로 변환하여 반환
        return UserInfoResponse.builder()
                .schoolEmail(updatedUser.getSchoolEmail())
                .nickname(updatedUser.getNickname())
                .gender(updatedUser.getGender())
                .studentId(updatedUser.getStudentId())
                .college(updatedUser.getCollege())
                .department(updatedUser.getDepartment())
                .build();
    }
}
