package com.metlab_project.backend.domain.dto.chatroom.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ChatRoomDetailResponse {
    private List<MemberResponse> members;
}
