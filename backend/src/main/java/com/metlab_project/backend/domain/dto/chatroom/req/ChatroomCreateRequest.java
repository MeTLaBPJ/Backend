package com.metlab_project.backend.domain.dto.chatroom.req;

import lombok.Getter;

@Getter
public class ChatroomCreateRequest {
    // 채팅룸 생성 ResponseDTO
    private String title;
    private String SubTitle;
    private String profileImage;
    private Integer membersCount;
    private String host;
}