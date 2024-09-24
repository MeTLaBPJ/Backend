package com.metlab_project.backend.domain.dto.user.req;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoRequest {
    private String schoolEmail;
    private String nickname;
    private String college;
    private String department;
    private String mbti;
    private String smoking;
    private String height;
    private String drinking;
    private String shortIntroduce;
    private String profile;

}
