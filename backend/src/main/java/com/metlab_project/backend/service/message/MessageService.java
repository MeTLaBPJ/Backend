package com.metlab_project.backend.service.message;

import com.metlab_project.backend.domain.entity.ChatRoom;
import com.metlab_project.backend.domain.entity.Message;
import com.metlab_project.backend.domain.entity.user.User;
import com.metlab_project.backend.repository.chatroom.ChatRoomRepository;
import com.metlab_project.backend.repository.message.MessageRepository;
import com.metlab_project.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public Message handleJoinMessage(Integer chatroomId, Message message, String schoolEmail) {
        Message savedMessage = settingMessage(message, chatroomId, schoolEmail);
        messageRepository.save(savedMessage);

        // TODO 에러핸들링
        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> null);
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> null);

        chatRoom.getUsers().add(user);

        if ("MALE".equals(user.getGender())) {
            chatRoom.setParticipantMaleCount(chatRoom.getParticipantMaleCount() + 1);
        } else if ("FEMALE".equals(user.getGender())) {
            chatRoom.setParticipantFemaleCount(chatRoom.getParticipantFemaleCount() + 1);
        }

        user.setTickets(user.getTickets() - 1);
        user.setChatRoom(chatRoom); // TODO 채팅룸 아이디 리스트로 바꾸기(User 객체)

        return savedMessage;
    }

    public Message handleLeaveMessage(Integer chatroomId, Message message, String schoolEmail) {
        Message savedMessage = settingMessage(message, chatroomId, schoolEmail);
        messageRepository.save(savedMessage);

        // TODO 에러핸들링
        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> null);
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> null);

        chatRoom.getUsers().remove(user);

        if ("MALE".equals(user.getGender())) {
            chatRoom.setParticipantMaleCount(chatRoom.getParticipantMaleCount() - 1);
        } else if ("FEMALE".equals(user.getGender())) {
            chatRoom.setParticipantFemaleCount(chatRoom.getParticipantFemaleCount() - 1);
        }

        user.setChatRoom(null); // TODO 채팅룸 아이디 리스트로 바꾸기(User 객체)

        return savedMessage;
    }

    public Message handleSendMessage(Integer chatroomId, Message message, String schoolEmail) {
        Message saveMessage = settingMessage(message, chatroomId, schoolEmail);
        return messageRepository.save(saveMessage);
    }

    public Message handleStartMessage(Integer chatroomId, Message message, String schoolEmail) {
        Message saveMessage = settingMessage(message, chatroomId, schoolEmail);

        // TODO 에러핸들링
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> null);


        int totalParticipants = chatRoom.getParticipantMaleCount() + chatRoom.getParticipantFemaleCount();
        if (totalParticipants != chatRoom.getTotalParticipant()) {
            return null; // TODO 에러핸들링
        }

        if (!chatRoom.getHost().equals(schoolEmail)) {
            return null; // TODO 에러핸들링
        }

        chatRoom.setStatus(ChatRoom.Status.ACTIVE);

        return messageRepository.save(saveMessage);
    }

    public Message settingMessage(Message message, Integer chatroomId, String schoolEmail) {
        // TODO 에러 핸들링
        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> null);
        String nickname = user.getNickname();

        message.setNickname(nickname);
        message.setSchoolEmail(schoolEmail);
        message.setChatroomId(chatroomId);

        return message;
    }
}