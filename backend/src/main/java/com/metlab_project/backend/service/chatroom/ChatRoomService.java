package com.metlab_project.backend.service.chatroom;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.metlab_project.backend.domain.dto.chatroom.req.ChatroomCreateRequest;
import com.metlab_project.backend.domain.dto.chatroom.res.ChatRoomDetailInChatResponse;
import com.metlab_project.backend.domain.dto.chatroom.res.ChatRoomDetailResponse;
import com.metlab_project.backend.domain.dto.chatroom.res.ChatRoomInfosResponse;
import com.metlab_project.backend.domain.dto.chatroom.res.ChatRoomMembersResponse;
import com.metlab_project.backend.domain.dto.chatroom.res.ChatRoomMessageResponse;
import com.metlab_project.backend.domain.dto.chatroom.res.ChatRoomResponse;
import com.metlab_project.backend.domain.dto.chatroom.res.MemberResponse;
import com.metlab_project.backend.domain.dto.user.res.UserInfoResponse;
import com.metlab_project.backend.domain.entity.ChatRoom;
import com.metlab_project.backend.domain.entity.Message;
import com.metlab_project.backend.domain.entity.user.User;
import com.metlab_project.backend.exception.CustomErrorCode;
import com.metlab_project.backend.exception.CustomException;
import com.metlab_project.backend.repository.chatroom.ChatRoomRepository;
import com.metlab_project.backend.repository.message.MessageRepository;
import com.metlab_project.backend.repository.user.UserRepository;
import com.metlab_project.backend.service.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private final UserService userService;

    public ChatRoomInfosResponse getAllChatrooms() {
        String schoolEmail = getUserEmail();
        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND, "User with Email " + schoolEmail + " not found"));

        List<ChatRoomResponse> rooms = chatRoomRepository.findAll().stream()
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
                        .status(chatRoom.getStatus().toString().toLowerCase())
                        .build())
                .collect(Collectors.toList());

        String gender = user.getGender().toString();
        Integer possibleEnterNumber = user.getTickets();

        return new ChatRoomInfosResponse(rooms, possibleEnterNumber, gender);
    }

    public ChatRoomMembersResponse getMembers(Integer chatroomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CHATROOM_NOT_FOUND, "Chat room with ID " + chatroomId + " not found"));

        List<MemberResponse> members = chatRoom.getUsers().stream()
                .map(this::convertToMemberResponse)
                .collect(Collectors.toList());

        return new ChatRoomMembersResponse(members);
    }

    private MemberResponse convertToMemberResponse(User user) {
        return new MemberResponse(
                user.getGender(),
                user.getDepartment(),
                user.getStudentId(),
                user.getNickname(),
                user.getProfile()
        );
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
                        chatRoom.getStatus().toString().toLowerCase()
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
                        user.getProfile()))
                .collect(Collectors.toList());

        return new ChatRoomDetailResponse(members);
    }

    public boolean createChatroom(ChatroomCreateRequest request) {
        String schoolEmail = getUserEmail();

        ChatRoom chatRoom = ChatRoom.builder()
                .chatroomName(request.getTitle())
                .title(request.getTitle())
                .subTitle(request.getSubTitle())
                .profileImage(request.getProfileImage())
                .maxMembers(request.getMaxMembers())
                .participantMaleCount(request.getMaleCount())
                .participantFemaleCount(request.getFemaleCount())
                .enterCheck(false)
                .host(schoolEmail)
                .totalParticipant(request.getMaxMembers())
                .status(ChatRoom.Status.WAITING)
                .build();

        chatRoomRepository.save(chatRoom);

        return true;
    }

    public void deleteChatroom(Integer chatroomId) {
        chatRoomRepository.deleteById(chatroomId);
    }

    public ChatRoomMessageResponse getChatMessages(Integer chatroomId) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CHATROOM_NOT_FOUND, "Chat room with ID " + chatroomId + " not found"));

        UserInfoResponse user =userService.getUserInfoBySchoolEmail();
        List<Message> messages = messageRepository.findByChatRoom_IdAndTypeOrderByCreatedAtAsc(chatroomId, Message.MessageType.CHAT);

        return new ChatRoomMessageResponse(chatRoom, user, messages);
    }

    public ChatRoomDetailInChatResponse getChatRoomDetailInChat(Integer chatroomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CHATROOM_NOT_FOUND, "Chat room with ID " + chatroomId + " not found"));

        List<MemberResponse> members = chatRoom.getUsers().stream()
                .map(user -> new MemberResponse(user.getGender(), user.getDepartment(), user.getStudentId(), user.getNickname(), user.getProfile()))
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
