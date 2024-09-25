package com.metlab_project.backend.domain.dto.chatroom.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponse {
    private String gender;
    private String major;
    private String studentId;
    private String nickname;
    private String profileImage;
}