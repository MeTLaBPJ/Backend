package com.metlab_project.backend.domain.dto.chatroom;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatroomSummaryResponse {
    // 채팅룸 정보 요약 조회 ResponseDTO
    // 1) 채팅룸 리스트 조회 시
    // 2) 채팅룸 인터페이스 정보 표기 시
    private String id; // ChatroomId
    private String chatroomName;
    private String hashtags;
    private Integer participantMaleCount;
    private Integer participantFemaleCount;
}