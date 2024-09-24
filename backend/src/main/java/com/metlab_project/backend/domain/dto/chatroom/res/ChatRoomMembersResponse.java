package com.metlab_project.backend.domain.dto.chatroom.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomMembersResponse {
    private List<MemberResponse> members;
}
