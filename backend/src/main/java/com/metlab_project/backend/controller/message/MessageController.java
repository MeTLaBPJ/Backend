package com.metlab_project.backend.controller.message;
import com.metlab_project.backend.service.message.MessageService;
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
    public Message joinChatroom(@DestinationVariable Integer chatroomId, @Payload Message message, Principal user){
        return  messageService.handleJoinMessage(chatroomId, message, user.getName());
    }

    @MessageMapping("/chat.leave/{chatroomId}")
    @SendTo("/sub/{chatroomId}")
    public Message leaveChatroom(@DestinationVariable Integer chatroomId, @Payload Message message, Principal user){
        return  messageService.handleLeaveMessage(chatroomId, message, user.getName());
    }

    @MessageMapping("/chat.send/{chatroomId}")
    @SendTo("/sub/{chatroomId}")
    public Message sendMessage(@DestinationVariable Integer chatroomId, @Payload Message message, Principal user){
        return  messageService.handleSendMessage(chatroomId, message, user.getName());
    }

    @MessageMapping("/chat.start/{chatroomId}")
    @SendTo("/sub/{chatroomId}")
    public Message sendMessage(@DestinationVariable Integer chatroomId, @Payload Message message, Principal user){
        return  messageService.handleStartMessage(chatroomId, message, user.getName());
    }
}
