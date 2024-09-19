package com.metlab_project.backend.domain.dto.user.res;

import com.metlab_project.backend.domain.entity.user.UserRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "School email cannot be blank")
    private String schoolEmail;

    @NotBlank(message = "Nickname cannot be blank")
    private String nickname;

    @NotBlank(message = "Gender cannot be blank")
    private String gender;

    @NotBlank(message = "StudentId cannot be blank")
    private String studentId;

    @NotBlank(message = "College cannot be blank")
    private String college;

    @NotBlank(message = "Department cannot be blank")
    private String department;

    private String birthday;

    private String mbti;
    private String shortIntroduce;
    private String height;
    private String drinking;
    private String smoking;
    private String profileImage;

    private UserRole role;
}