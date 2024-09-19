package com.metlab_project.backend.service.chatroom;

import com.metlab_project.backend.domain.dto.chatroom.res.ChatRoomDetailInChatResponse;
import com.metlab_project.backend.domain.dto.chatroom.res.ChatRoomDetailResponse;
import com.metlab_project.backend.domain.dto.chatroom.res.ChatRoomResponse;
import com.metlab_project.backend.domain.dto.chatroom.req.ChatroomCreateRequest;
import com.metlab_project.backend.domain.dto.chatroom.res.MemberResponse;
import com.metlab_project.backend.domain.entity.ChatRoom;
import com.metlab_project.backend.domain.entity.Message;
import com.metlab_project.backend.domain.entity.user.User;
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
        Integer currentUserId = getCurrentUserId();
        List<ChatRoom> myChatRooms = chatRoomRepository.findAll().stream()
                .filter(chatRoom -> chatRoom.getUsers().stream()
                        .anyMatch(user -> user.getId().equals(currentUserId)))
                .collect(Collectors.toList());

        return myChatRooms.stream()
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


    public ChatRoomDetailResponse getChatRoomDetail(Integer roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅룸을 찾을 수 없습니다."));

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        ChatRoom chatRoom = ChatRoom.builder()
                .chatroomName(request.getTitle())
                .title(request.getTitle())
                .subTitle(request.getSubTitle())
                .profileImage(request.getProfileImage())
                .maxMembers(request.getMaleCount() + request.getFemaleCount())
                .participantMaleCount(request.getMaleCount())
                .participantFemaleCount(request.getFemaleCount())
                .enterCheck(false)
                .host(currentUser.getSchoolEmail())
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
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        Integer currentUserId = getCurrentUserId();
        boolean isParticipant = chatRoom.getUsers().stream()
                .anyMatch(user -> user.getId().equals(currentUserId));

        if (!isParticipant) {
            throw new SecurityException("채팅방에 참여하지 않은 사용자는 채팅 내역을 볼 수 없습니다.");
        }

        return messageRepository.findByChatRoom_IdAndTypeOrderByCreatedAtAsc(chatroomId, Message.MessageType.CHAT);
    }

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }

    public ChatRoomDetailInChatResponse getChatRoomDetailInChat(int roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방을 찾을 수 없습니다."));

        Integer currentUserId = getCurrentUserId();
        boolean isParticipant = chatRoom.getUsers().stream()
                .anyMatch(user -> user.getId().equals(currentUserId));

        if (!isParticipant) {
            throw new SecurityException("채팅방에 참여하지 않은 사용자는 접근할 수 없습니다.");
        }

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
}
