package com.metlab_project.backend.domain.dto.chatroom.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomResponse {
    private Integer id;
    private String title;
    private String subTitle;
    private Integer maxMembers;
    private Boolean enterCheck;
    private String host;
    private Integer maleCount;
    private Integer femaleCount;
    private String profileImage;
    private Boolean hasStarted;
}
