package com.metlab_project.backend.domain.dto.user;

import com.metlab_project.backend.domain.entity.User;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class UserInfoResponse {
    private String schoolEmail;
    private String nickname;
    private String gender;
    private String studentId;
    private String college;
    private String department;
    private UserRole role;

    public static UserInfoResponse from(User user){
        return UserInfoResponse.builder()
        .schoolEmail(user.getSchoolEmail())
        .nickname(user.getNickname())
        .gender(user.getGender())
        .studentId(user.getStudentId())
        .college(user.getCollege())
        .department(user.getDepartment())
        .role(user.getRole())
        .build();
    }
}
