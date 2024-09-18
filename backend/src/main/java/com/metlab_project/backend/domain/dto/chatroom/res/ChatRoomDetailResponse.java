package com.metlab_project.backend.domain.dto.chatroom.res;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomDetailResponse {
    private Integer id;
    private String name;
    private String title;
    private String subTitle;
    private List<MemberResponse> members;
    private Integer maxMembers;
    private Boolean enterCheck;
    private String host;
    private Integer maleCount;
    private Integer femaleCount;
}
