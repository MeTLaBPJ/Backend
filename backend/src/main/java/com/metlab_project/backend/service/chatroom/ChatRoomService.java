package com.metlab_project.backend.service.chatroom;

import com.metlab_project.backend.domain.dto.chatroom.res.ChatRoomDetailInChatResponse;
import com.metlab_project.backend.domain.dto.chatroom.res.ChatRoomDetailResponse;
import com.metlab_project.backend.domain.dto.chatroom.res.ChatRoomResponse;
import com.metlab_project.backend.domain.dto.chatroom.req.ChatroomCreateRequest;
import com.metlab_project.backend.domain.dto.chatroom.res.MemberResponse;
import com.metlab_project.backend.domain.entity.ChatRoom;
import com.metlab_project.backend.domain.entity.Message;
import com.metlab_project.backend.domain.entity.user.User;
import com.metlab_project.backend.exception.CustomErrorCode;
import com.metlab_project.backend.exception.CustomException;
import com.metlab_project.backend.repository.chatroom.ChatRoomRepository;
import com.metlab_project.backend.repository.message.MessageRepository;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    public List<ChatRoomResponse> getAllChatrooms() {
        return chatRoomRepository.findAll().stream()
                .map(chatRoom -> ChatRoomResponse.builder()
                        .id(chatRoom.getId())
                        .title(chatRoom.getTitle())
                        .subTitle(chatRoom.getSubTitle())
                        .maxMembers(chatRoom.getMaxMembers())
                        .enterCheck(chatRoom.getEnterCheck())
                        .host(chatRoom.getHost())
                        .maleCount(chatRoom.getParticipantMaleCount())
                        .femaleCount(chatRoom.getParticipantFemaleCount())
                        .profileImage(chatRoom.getProfileImage())
                        .hasStarted(chatRoom.getStatus() == ChatRoom.Status.ACTIVE)
                        .build())
                .collect(Collectors.toList());
    }

    public List<ChatRoomResponse> getMyChatrooms() {
        String schoolEmail = getUserEmail();
        List<ChatRoom> chatrooms = chatRoomRepository.findAll().stream()
                .filter(chatRoom -> chatRoom.getUsers().stream()
                        .anyMatch(user -> user.getSchoolEmail().equals(schoolEmail)))
                .collect(Collectors.toList());

        return chatrooms.stream()
                .map(chatRoom -> new ChatRoomResponse(
                        chatRoom.getId(),
                        chatRoom.getTitle(),
                        chatRoom.getSubTitle(),
                        chatRoom.getMaxMembers(),
                        chatRoom.getEnterCheck(),
                        chatRoom.getHost(),
                        chatRoom.getParticipantMaleCount(),
                        chatRoom.getParticipantFemaleCount(),
                        chatRoom.getProfileImage(),
                        chatRoom.getStatus() == ChatRoom.Status.ACTIVE
                ))
                .collect(Collectors.toList());
    }


    public ChatRoomDetailResponse getChatRoomDetail(Integer chatroomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CHATROOM_NOT_FOUND, "Chat room with ID " + chatroomId + " not found"));

        List<MemberResponse> members = chatRoom.getUsers().stream()
                .map(user -> new MemberResponse(
                        user.getGender(),
                        user.getDepartment(),
                        user.getStudentId(),
                        user.getNickname(),
                        user.getProfileImage()))
                .collect(Collectors.toList());

        return new ChatRoomDetailResponse(members);
    }

    public ChatRoomResponse createChatroom(ChatroomCreateRequest request) {
        String schoolEmail = getUserEmail();

        ChatRoom chatRoom = ChatRoom.builder()
                .chatroomName(request.getTitle())
                .title(request.getTitle())
                .subTitle(request.getSubTitle())
                .profileImage(request.getProfileImage())
                .maxMembers(request.getMaleCount() + request.getFemaleCount())
                .participantMaleCount(request.getMaleCount())
                .participantFemaleCount(request.getFemaleCount())
                .enterCheck(false)
                .host(schoolEmail)
                .totalParticipant(request.getFemaleCount()*2)
                .status(ChatRoom.Status.WAITING)
                .build();

        chatRoomRepository.save(chatRoom);

        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .title(chatRoom.getTitle())
                .subTitle(chatRoom.getSubTitle())
                .maxMembers(chatRoom.getMaxMembers())
                .enterCheck(chatRoom.getEnterCheck())
                .host(chatRoom.getHost())
                .maleCount(chatRoom.getParticipantMaleCount())
                .femaleCount(chatRoom.getParticipantFemaleCount())
                .profileImage(chatRoom.getProfileImage())
                .hasStarted(chatRoom.getStatus() == ChatRoom.Status.ACTIVE)
                .build();
    }

    public void deleteChatroom(Integer chatroomId) {
        chatRoomRepository.deleteById(chatroomId);
    }

    public List<Message> getChatMessages(Integer chatroomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CHATROOM_NOT_FOUND, "Chat room with ID " + chatroomId + " not found"));

//        String schoolEmail = getUserEmail();
//
//        if (!chatRoom.getUsers().stream().anyMatch(user -> user.getSchoolEmail().equals(schoolEmail))) {
//            throw new CustomException(CustomErrorCode.NO_AUTHORITY_IN_CHATROOM, "");
//        }

        return messageRepository.findByChatRoom_IdAndTypeOrderByCreatedAtAsc(chatroomId, Message.MessageType.CHAT);
    }

    public ChatRoomDetailInChatResponse getChatRoomDetailInChat(Integer chatroomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CHATROOM_NOT_FOUND, "Chat room with ID " + chatroomId + " not found"));

//        String schoolEmail = getUserEmail();
//
////        if (!chatRoom.getUsers().stream().anyMatch(user -> user.getSchoolEmail().equals(schoolEmail))) {
////            throw new CustomException(CustomErrorCode.NO_AUTHORITY_IN_CHATROOM, "");
////        }

        List<MemberResponse> members = chatRoom.getUsers().stream()
                .map(user -> new MemberResponse(user.getGender(), user.getDepartment(), user.getStudentId(), user.getNickname(), user.getProfileImage()))
                .toList();

        int possibleEnterNumber = chatRoom.getMaxMembers() - chatRoom.getTotalParticipant();

        return ChatRoomDetailInChatResponse.builder()
                .id(chatRoom.getId())
                .title(chatRoom.getTitle())
                .members(members)
                .profileImage(chatRoom.getProfileImage())
                .possibleEnterNumber(possibleEnterNumber)
                .deadline(chatRoom.getDeadline())
                .build();
    }

    private String getUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
