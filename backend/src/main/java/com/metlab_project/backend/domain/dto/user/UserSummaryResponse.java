package com.metlab_project.backend.domain.dto.user;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSummaryResponse {
    // 채팅룸 접속 유저 요약 조회 정보
    private String id;
    private String schoolEmail;
    private String nickname;
    private String gender;
    private String studentId;
    private String college;
    private String department;
    private String mbti;
}