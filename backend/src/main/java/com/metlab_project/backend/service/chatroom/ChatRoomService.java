package com.metlab_project.backend.service.chatroom;

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
                user.getMajor(),
                user.getStudentId(),
                user.getNickname(),
                user.getProfileImage()))
            .collect(Collectors.toList());

        long maleCount = members.stream().filter(member -> "Male".equalsIgnoreCase(member.getGender())).count();
        long femaleCount = members.stream().filter(member -> "Female".equalsIgnoreCase(member.getGender())).count();

        return new ChatRoomDetailResponse(
            chatRoom.getId(),
            chatRoom.getChatroomName(),
            chatRoom.getTitle(),
            chatRoom.getSubTitle(),
            members,
            chatRoom.getMaxMembers(),
            chatRoom.getEnterCheck(),
            chatRoom.getHost(),
            (int) maleCount,
            (int) femaleCount
        );
    }

    public ChatRoomResponse createChatroom(ChatroomCreateRequest request) {
        ChatRoom chatRoom = ChatRoom.builder()
            .chatroomName(request.getTitle())
            .title(request.getTitle())
            .subTitle(request.getSubTitle())
            .profileImage(request.getProfileImage())
            .maxMembers(request.getMembersCount())
            .enterCheck(false)
            .host(request.getHost())
            .status(ChatRoom.Status.WAITING)
            .build();
        chatRoomRepository.save(chatRoom);
    
        return new ChatRoomResponse(
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
        );
    }

    public void deleteChatroom(Integer chatroomId) {
        chatRoomRepository.deleteById(chatroomId);
    }

    public List<Message> getChatMessages(Integer chatroomId) {
        return messageRepository.findByChatroomIdAndTypeOrderByCreatedAtAsc(chatroomId, Message.MessageType.CHAT);
    }

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}
