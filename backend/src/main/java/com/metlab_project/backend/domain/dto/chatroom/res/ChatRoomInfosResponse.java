package com.metlab_project.backend.domain.dto.chatroom.res;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomInfosResponse {
    private List<ChatRoomResponse> rooms;
    private Integer possibleEnterNumber;
    private String gender;
}