package com.metlab_project.backend.domain.dto.user.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailResponse {
    private String schoolEmail;
    private String nickname;
    private String gender;
    private String studentId;
    private String college;
    private String department;
    private String mbti;
    private String shortIntroduce;
    private String height;
    private String drinking;
    private String smoking;
    private String profileImage;
}
