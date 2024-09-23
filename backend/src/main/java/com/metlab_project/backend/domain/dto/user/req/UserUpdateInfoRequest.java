package com.metlab_project.backend.domain.dto.user.req;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
public class UserUpdateInfoRequest {

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
}
