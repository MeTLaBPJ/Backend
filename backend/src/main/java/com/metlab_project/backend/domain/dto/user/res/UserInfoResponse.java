package com.metlab_project.backend.domain.dto.user.res;

import com.metlab_project.backend.domain.entity.user.UserRole;
import lombok.*;

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

    private String schoolEmail;
    private String nickname;
    private String gender;

    private String studentId;
    private String college;
    private String department;
    private String birthday;
    private String mbti;

    private UserRole role;
}
