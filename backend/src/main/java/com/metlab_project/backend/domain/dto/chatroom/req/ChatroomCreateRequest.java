package com.metlab_project.backend.domain.dto.chatroom.req;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatroomCreateRequest {
    // 채팅룸 생성 ResponseDTO
    private String chatroomName;
    private LocalDateTime deadline;
    private String hashtags;
    private Integer maxUser;
    private String host;
}