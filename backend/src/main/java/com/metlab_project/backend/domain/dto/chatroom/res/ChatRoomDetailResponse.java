package com.metlab_project.backend.domain.dto.chatroom.res;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomDetailResponse {
    private List<MemberResponse> members;
}