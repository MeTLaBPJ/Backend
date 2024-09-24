package com.metlab_project.backend.domain.dto.chatroom.res;

import com.metlab_project.backend.domain.dto.user.res.UserInfoResponse;
import com.metlab_project.backend.domain.entity.ChatRoom;
import com.metlab_project.backend.domain.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomMessageResponse {
    private ChatRoom chatRoom;
    private UserInfoResponse user;
    private List<Message> messages;
}
