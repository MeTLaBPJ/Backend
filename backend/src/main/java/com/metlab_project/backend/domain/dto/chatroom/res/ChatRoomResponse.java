package com.metlab_project.backend.domain.dto.chatroom.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

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

    public ChatRoomResponse(Integer id, String title, String subTitle, Integer maxMembers, Boolean enterCheck, String host, Integer maleCount, Integer femaleCount, String profileImage, Boolean hasStarted) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.maxMembers = maxMembers;
        this.enterCheck = enterCheck;
        this.host = host;
        this.maleCount = maleCount;
        this.femaleCount = femaleCount;
        this.profileImage = profileImage;
        this.hasStarted = hasStarted;
    }
}
