package com.metlab_project.backend.controller.message;

import com.metlab_project.backend.domain.dto.message.req.MessageDTO;
import com.metlab_project.backend.service.message.MessageService;
import com.metlab_project.backend.domain.entity.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/chat/join/{chatroomId}")
    @SendTo("/sub/{chatroomId}")
    public Message joinChatroom(@DestinationVariable Integer chatroomId, @Payload MessageDTO messageDTO, Principal user) {
        log.info("Join chatroom request received - chatroomId: {}, user: {}, message: {}", chatroomId, user.getName(), messageDTO);
        Message result = messageService.handleJoinMessage(chatroomId, messageDTO, user.getName());
        log.info("Join chatroom response - result: {}", result);
        return result;
    }

    @MessageMapping("/chat/leave/{chatroomId}")
    @SendTo("/sub/{chatroomId}")
    public Message leaveChatroom(@DestinationVariable Integer chatroomId, @Payload MessageDTO messageDTO, Principal user) {
        log.info("Leave chatroom request received - chatroomId: {}, user: {}, message: {}", chatroomId, user.getName(), messageDTO);
        Message result = messageService.handleLeaveMessage(chatroomId, messageDTO, user.getName());
        log.info("Leave chatroom response - result: {}", result);
        return result;
    }

    @MessageMapping("/chat/send/{chatroomId}")
    @SendTo("/sub/{chatroomId}")
    public Message sendMessage(@DestinationVariable Integer chatroomId, @Payload MessageDTO messageDTO, Principal user) {
        log.info("Send message request received - chatroomId: {}, user: {}, message: {}", chatroomId, user.getName(), messageDTO);
        Message result = messageService.handleSendMessage(chatroomId, messageDTO, user.getName());
        log.info("Send message response - result: {}", result);
        return result;
    }

    @MessageMapping("/chat/start/{chatroomId}")
    @SendTo("/sub/{chatroomId}")
    public Message startChatroom(@DestinationVariable Integer chatroomId, @Payload MessageDTO messageDTO, Principal user) {
        log.info("Start chatroom request received - chatroomId: {}, user: {}, message: {}", chatroomId, user.getName(), messageDTO);
        Message result = messageService.handleStartMessage(chatroomId, messageDTO, user.getName());
        log.info("Start chatroom response - result: {}", result);
        return result;
    }
}