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

    @Builder
    public MyPageResponseDto( String schoolEmail, String nickname,String gender,String studentId, String department,String mbti){
        this.schoolEmail = schoolEmail;
        this.nickname = nickname;
        this.gender = gender;
        this.studentId = studentId;
        this.department = department;
        this.mbti = mbti;}
    }
