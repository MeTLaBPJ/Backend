package com.metlab_project.backend.domain.dto.chatroom.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChatroomCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be less than 100 characters")
    private String title;

    @NotBlank(message = "SubTitle is required")
    @Size(max = 100, message = "SubTitle must be less than 100 characters")
    private String subTitle;

    private String profileImage;

    @NotNull(message = "MaxMembers is required")
    private Integer maxMembers;

    @NotNull(message = "MaleCount is required")
    private Integer maleCount;

    @NotNull(message = "FemaleCount is required")
    private Integer femaleCount;
}