package com.metlab_project.backend.service.message;

import com.metlab_project.backend.domain.entity.ChatRoom;
import com.metlab_project.backend.domain.entity.Message;
import com.metlab_project.backend.domain.entity.user.User;
import com.metlab_project.backend.exception.CustomErrorCode;
import com.metlab_project.backend.exception.CustomException;
import com.metlab_project.backend.repository.chatroom.ChatRoomRepository;
import com.metlab_project.backend.repository.message.MessageRepository;
import com.metlab_project.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public Message handleJoinMessage(Integer chatroomId, Message message, String schoolEmail) {
        Message savedMessage = settingMessage(message, chatroomId, schoolEmail);
        messageRepository.save(savedMessage);

        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND, "User with Email " + schoolEmail + " not found"));
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CHATROOM_NOT_FOUND, "Chat room with ID " + chatroomId + " not found"));

        user.addChatRoom(chatRoom);

        if ("MALE".equals(user.getGender())) {
            chatRoom.setParticipantMaleCount(chatRoom.getParticipantMaleCount() + 1);
        } else if ("FEMALE".equals(user.getGender())) {
            chatRoom.setParticipantFemaleCount(chatRoom.getParticipantFemaleCount() + 1);
        }

        user.setTickets(user.getTickets() - 1);

        userRepository.save(user);
        chatRoomRepository.save(chatRoom);

        return savedMessage;
    }

    @Transactional
    public Message handleLeaveMessage(Integer chatroomId, Message message, String schoolEmail) {
        Message savedMessage = settingMessage(message, chatroomId, schoolEmail);
        messageRepository.save(savedMessage);

        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND, "User with Email " + schoolEmail + " not found"));
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CHATROOM_NOT_FOUND, "Chat room with ID " + chatroomId + " not found"));

        user.removeChatRoom(chatRoom);

        if ("MALE".equals(user.getGender())) {
            chatRoom.setParticipantMaleCount(chatRoom.getParticipantMaleCount() - 1);
        } else if ("FEMALE".equals(user.getGender())) {
            chatRoom.setParticipantFemaleCount(chatRoom.getParticipantFemaleCount() - 1);
        }

        userRepository.save(user);
        chatRoomRepository.save(chatRoom);

        return savedMessage;
    }

    @Transactional
    public Message handleSendMessage(Integer chatroomId, Message message, String schoolEmail) {
        Message savedMessage = settingMessage(message, chatroomId, schoolEmail);
        return messageRepository.save(savedMessage);
    }

    @Transactional
    public Message handleStartMessage(Integer chatroomId, Message message, String schoolEmail) {
        Message savedMessage = settingMessage(message, chatroomId, schoolEmail);

        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CHATROOM_NOT_FOUND, "Chat room with ID " + chatroomId + " not found"));

        int totalParticipants = chatRoom.getParticipantMaleCount() + chatRoom.getParticipantFemaleCount();
        if (totalParticipants != chatRoom.getTotalParticipant()) {
            throw new CustomException(CustomErrorCode.INSUFFICIENT_PARTICIPANTS, "Need " + chatRoom.getTotalParticipant() + " participants to start, but only " + totalParticipants + " are present");
        }

        if (!chatRoom.getHost().equals(schoolEmail)) {
            throw new CustomException(CustomErrorCode.NOT_CHATROOM_HOST, "User " + schoolEmail + " is not the host of chat room " + chatroomId);
        }

        chatRoom.setStatus(ChatRoom.Status.ACTIVE);
        chatRoomRepository.save(chatRoom);

        return messageRepository.save(savedMessage);
    }

    private Message settingMessage(Message message, Integer chatroomId, String schoolEmail) {
        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND, "User with Email " + schoolEmail + " not found"));

        message.setNickname(user.getNickname());
        message.setSchoolEmail(schoolEmail);
        message.setChatroomId(chatroomId);

        return message;
    }
}