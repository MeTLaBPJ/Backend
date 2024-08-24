package com.metlab_project.backend.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    // 유저 정보 조회
    private String schoolEmail;
    private String nickname;
    private String gender;

    private String studentId;
    private String college;
    private String department;
}
