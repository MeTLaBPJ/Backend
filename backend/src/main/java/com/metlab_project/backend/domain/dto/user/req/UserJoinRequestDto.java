package com.metlab_project.backend.domain.dto.user.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserJoinRequestDto {
    @Schema(description = "유저의 학교메일", example = "test")
    @NotNull(message = "schoolEmail is null")
    @NotEmpty(message = "schoolEmail is empty")
    private String schoolEmail;

    @Schema(description = "유저의 비밀번호", example = "test1234")
    @NotNull(message = "password is null")
    @NotEmpty(message = "password is empty")
    private String password;

    @Schema(description = "유저의 닉네임", example = "홍길동")
    @NotNull(message = "nickname is null")
    @NotEmpty(message = "nickname is empty")
    private String nickname;


    @Schema(description = "유저의 성별", example = "male")
    @NotNull(message = "gender is null")
    @NotEmpty(message = "gender is empty")
    private String gender;

    @Schema(description = "유저의 학번", example = "202201535")
    @NotNull(message = "studentId is null")
    @NotEmpty(message = "studentId is empty")
    private String studentId;

    @Schema(description = "유저의 단과대", example = "정보기술대학")
    @NotNull(message = "college is null")
    @NotEmpty(message = "college is empty")
    private String college;

    @Schema(description = "유저의 학과", example = "컴퓨터공학부")
    @NotNull(message = "department is null")
    @NotEmpty(message = "department is empty")
    private String department;

    @Schema(description = "유저의 생일", example = "2003-06-24")
    private String birthday;

    @Schema(description = "유저의 프로필", example = "0")
    @NotNull(message = "profile is null")
    @NotEmpty(message = "profile is empty")
    private String profile;
   
}
