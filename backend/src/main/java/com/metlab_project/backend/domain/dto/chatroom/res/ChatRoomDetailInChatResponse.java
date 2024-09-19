package com.metlab_project.backend.domain.dto.chatroom.res;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDetailInChatResponse {
    private int id;
    private String title;
    private List<MemberResponse> members;
    private String profileImage;
    private int possibleEnterNumber;
    private LocalDateTime deadline;
}

