package com.metlab_project.backend.domain.dto.user;

import com.metlab_project.backend.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private String schoolEmail;
    private String nickname;
    private User.Gender gender;

    private String studentId;
    private String college;
    private String department;
}
