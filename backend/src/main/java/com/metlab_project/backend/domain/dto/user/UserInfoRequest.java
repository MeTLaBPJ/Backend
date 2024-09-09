package com.metlab_project.backend.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoRequest {
    private String schoolEmail;
    private String password;
    private String nickname;
    private String birthday;
    private String gender;

    private String studentId;
    private String college;
    private String department;
}