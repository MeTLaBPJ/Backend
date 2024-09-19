package com.metlab_project.backend.controller.chatroom;

import java.util.List;

import com.metlab_project.backend.domain.dto.chatroom.res.ChatRoomDetailInChatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metlab_project.backend.domain.dto.chatroom.req.ChatroomCreateRequest;
import com.metlab_project.backend.domain.dto.chatroom.res.ChatRoomResponse;
import com.metlab_project.backend.domain.dto.chatroom.res.MemberResponse;
import com.metlab_project.backend.domain.entity.Message;
import com.metlab_project.backend.service.chatroom.ChatRoomService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
public class ChatRoomController {
    
    private final ChatRoomService chatRoomService;

    @GetMapping("/list")
    @Operation(summary = "모든 채팅룸 불러오기", description = "모든 채팅룸을 불러옵니다.")
    public ResponseEntity<?> getAllChatroom() {
        List<ChatRoomResponse> response = chatRoomService.getAllChatrooms();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/participants/{chatroomid}")
    @Operation(summary = "채팅룸 참여 유저 불러오기", description = "특정 채팅룸의 참여자 목록을 불러옵니다.")
    public ResponseEntity<?> getParticipants(@PathVariable("chatroomid") Integer chatroomId) {
        List<MemberResponse> response = chatRoomService.getChatRoomDetail(chatroomId).getMembers();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "채팅룸 생성하기", description = "채팅룸을 생성합니다.")
    public ResponseEntity<?> createChatroom(@Valid @RequestBody ChatroomCreateRequest request) {
        // TODO @Valid 사용 시 유효성 검증 실패 시 MethodArgumentNotValidException 발생 가능
        // TODO ChatroomCreateRequest @Valid 적용
        ChatRoomResponse response = chatRoomService.createChatroom(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{chatroomid}")
    @Operation(summary = "채팅룸 삭제하기", description = "특정 채팅룸을 삭제합니다.")
    public ResponseEntity<Void> deleteChatroom(@PathVariable("chatroomId") Integer chatroomId) {
        // TODO 채팅룸 삭제 권한 체크
        chatRoomService.deleteChatroom(chatroomId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{chatroomid}/messages")
    @Operation(summary = "채팅룸 내역 가져오기", description = "특정 채팅룸의 채팅 내역을 가져옵니다.")
    public ResponseEntity<List<Message>> getChatMessages(@PathVariable("chatroomId") Integer chatroomId) {
        List<Message> messages = chatRoomService.getChatMessages(chatroomId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{chatroomId}")
    @Operation(summary = "특정 채팅방 상세 정보 가져오기", description = "특정 채팅방의 상세 정보를 반환합니다.")
    public ResponseEntity<?> getChatRoomDetailInChat(@PathVariable("chatroomId") int chatroomId) {
        ChatRoomDetailInChatResponse response = chatRoomService.getChatRoomDetailInChat(chatroomId);
        return ResponseEntity.ok(response);
    }

}
