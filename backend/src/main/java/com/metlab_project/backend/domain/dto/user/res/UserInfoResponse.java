package com.metlab_project.backend.domain.dto.user.res;

import com.metlab_project.backend.domain.entity.user.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    public UserInfoResponse(String schoolEmail, UserRole role) {
        this.schoolEmail = schoolEmail;
        this.role = role;
    }
        
    public UserInfoResponse(String schoolEmail, String nickname, UserRole role) {
        this.schoolEmail = schoolEmail;
        this.nickname = nickname;
        this.role = role;
    }

    private String schoolEmail;
    private String nickname;
    private String gender;

    private String studentId;
    private String college;
    private String department;
    private String birthday;
    private String mbti;
    private String smoking;
    private String height;
    private String drinking;
    private String shortIntroduce;
    private String profile;

    private UserRole role;
}