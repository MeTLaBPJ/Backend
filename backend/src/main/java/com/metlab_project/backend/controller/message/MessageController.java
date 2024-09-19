package com.metlab_project.backend.controller.message;
import com.metlab_project.backend.service.message.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import com.metlab_project.backend.domain.entity.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/chat.join/{chatroomId}")
    @SendTo("/sub/{chatroomId}")
    @Operation(summary = "입장 메시지 전송 및 처리", description = "입장 메시지를 전송하고 입장 로직을 수행합니다.")
    public Message joinChatroom(@DestinationVariable Integer chatroomId, @Payload Message message, Principal user){
        return  messageService.handleJoinMessage(chatroomId, message, user.getName());
    }

    @MessageMapping("/chat.leave/{chatroomId}")
    @SendTo("/sub/{chatroomId}")
    @Operation(summary = "퇴장 메시지 전송 및 처리", description = "퇴장 메시지를 전송하고 퇴장 로직을 수행합니다.")
    public Message leaveChatroom(@DestinationVariable Integer chatroomId, @Payload Message message, Principal user){
        return  messageService.handleLeaveMessage(chatroomId, message, user.getName());
    }

    @MessageMapping("/chat.send/{chatroomId}")
    @SendTo("/sub/{chatroomId}")
    @Operation(summary = "채팅 메시지 전송", description = "채팅 메시지를 전송하고 전송 로직을 수행합니다.")
    public Message sendMessage(@DestinationVariable Integer chatroomId, @Payload Message message, Principal user){
        return  messageService.handleSendMessage(chatroomId, message, user.getName());
    }

    @MessageMapping("/chat.start/{chatroomId}")
    @SendTo("/sub/{chatroomId}")
    @Operation(summary = "시작 메시지 전송 ", description = "채팅방을 활성화하고 시작 메시지를 전송합니다.")
    public Message startChatroom(@DestinationVariable Integer chatroomId, @Payload Message message, Principal user){
        return  messageService.handleStartMessage(chatroomId, message, user.getName());
    }
}
