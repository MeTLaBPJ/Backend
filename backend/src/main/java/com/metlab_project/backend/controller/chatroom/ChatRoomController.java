package com.metlab_project.backend.controller.chatroom;

import com.metlab_project.backend.domain.dto.chatroom.req.ChatroomCreateRequest;
import com.metlab_project.backend.domain.dto.chatroom.res.ChatRoomResponse;
import com.metlab_project.backend.domain.dto.chatroom.res.MemberResponse;
import com.metlab_project.backend.domain.entity.Message;
import com.metlab_project.backend.domain.entity.user.User;
import com.metlab_project.backend.domain.dto.chatroom.res.ChatRoomDetailResponse;
import com.metlab_project.backend.service.chatroom.ChatRoomService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
public class ChatRoomController {
    
    private final ChatRoomService chatRoomService;

    @GetMapping("/list")
    @Operation(summary = "모든 채팅룸 불러오기", description = "모든 채팅룸을 불러옵니다.")
    public ResponseEntity<?> getAllChatroom() {
        List<ChatRoomResponse> allChatRooms = chatRoomService.getAllChatrooms();
        return ResponseEntity.ok(allChatRooms);
    }

    @GetMapping("/participants/{chatroomid}")
    @Operation(summary = "채팅룸 참여 유저 불러오기", description = "특정 채팅룸의 참여자 목록을 불러옵니다.")
    public ResponseEntity<?> getParticipants(@PathVariable("chatroomid") Integer chatroomId) {
         List<MemberResponse> members = chatRoomService.getChatRoomDetail(chatroomId).getMembers();
        return ResponseEntity.ok(members);
    }

    @PostMapping
    @Operation(summary = "채팅룸 생성하기", description = "채팅룸을 생성합니다.")
    public ResponseEntity createChatroom(@Valid @RequestBody ChatroomCreateRequest request) {
        // @Valid 사용 시 유효성 검증 실패 시 MethodArgumentNotValidException 발생 가능
        ChatRoomResponse newChatRoom = chatRoomService.createChatroom(request);
        return ResponseEntity.ok(newChatRoom);
    }

    @DeleteMapping("/{chatroomId}")
    @Operation(summary = "채팅룸 삭제하기", description = "특정 채팅룸을 삭제합니다.")
    public ResponseEntity<Void> deleteChatroom(@PathVariable("chatroomId") Integer chatroomId) {
        chatRoomService.deleteChatroom(chatroomId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/{chatroomId}/messages")
    @Operation(summary = "채팅룸 내역 가져오기", description = "특정 채팅룸의 채팅 내역을 가져옵니다.")
    public ResponseEntity<List<Message>> getChatMessages(@PathVariable("chatroomId") Integer chatroomId) {
        List<Message> messages = chatRoomService.getChatMessages(chatroomId);
        return ResponseEntity.ok(messages);
    }
}
