package com.metlab_project.backend.domain.dto.chatroom.res;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomMembersResponse {
    private List<MemberResponse> members;
}