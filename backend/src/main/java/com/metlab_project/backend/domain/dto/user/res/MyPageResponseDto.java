package com.metlab_project.backend.domain.dto.user.res;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyPageResponseDto {
    private String schoolEmail;
    private String nickname;
    private String gender;

    private String studentId;
    private String department;
    private String mbti;
    private String height;
    private String smoking;
    private String drinking;

    @Builder
    public MyPageResponseDto( String schoolEmail, String nickname,String gender,String studentId, String department,String mbti,String height, String smoking, String drinking){
        this.schoolEmail = schoolEmail;
        this.nickname = nickname;
        this.gender = gender;
        this.studentId = studentId;
        this.department = department;
        this.mbti = mbti;
        this.height=height;
        this.drinking=drinking;
        this.smoking=smoking;
    }
}