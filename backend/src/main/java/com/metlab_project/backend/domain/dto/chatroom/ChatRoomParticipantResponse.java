package com.metlab_project.backend.domain.dto.chatroom;

import com.metlab_project.backend.domain.dto.user.UserSummaryResponse;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatroomParticipantResponse {
    // 채팅룸 참여자 조회 ResponseDTO
    private List<UserSummaryResponse> maleUsers;
    private List<UserSummaryResponse> femaleUsers;
}