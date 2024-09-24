package com.metlab_project.backend.domain.dto.message.req;

import com.metlab_project.backend.domain.entity.Message;

import lombok.Data;

@Data
public class MessageDTO {
    private Message.MessageType type;
    private String content;
}
