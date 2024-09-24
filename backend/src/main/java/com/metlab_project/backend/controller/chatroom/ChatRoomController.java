package com.metlab_project.backend.controller.chatroom;

import java.util.List;

import com.metlab_project.backend.domain.dto.chatroom.res.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metlab_project.backend.domain.dto.chatroom.req.ChatroomCreateRequest;
import com.metlab_project.backend.domain.entity.Message;
import com.metlab_project.backend.service.chatroom.ChatRoomService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
public class ChatRoomController {
    
    private final ChatRoomService chatRoomService;

    @GetMapping("/list")
    @Operation(summary = "모든 채팅룸 불러오기", description = "모든 채팅룸을 불러옵니다.")
    public ResponseEntity<?> getAllChatroom() {
        ChatRoomInfosResponse response = chatRoomService.getAllChatrooms();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/participants/{chatroomid}")
    @Operation(summary = "채팅룸 참여 유저 불러오기", description = "특정 채팅룸의 참여자 목록을 불러옵니다.")
    public ResponseEntity<?> getParticipants(@PathVariable("chatroomid") Integer chatroomId) {
        ChatRoomMembersResponse response = chatRoomService.getMembers(chatroomId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "채팅룸 생성하기", description = "채팅룸을 생성합니다.")
    public ResponseEntity<?> createChatroom(@Valid @RequestBody ChatroomCreateRequest request) {
        boolean response = chatRoomService.createChatroom(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{chatroomid}")
    @Operation(summary = "채팅룸 삭제하기", description = "특정 채팅룸을 삭제합니다.")
    public ResponseEntity<?> deleteChatroom(@PathVariable("chatroomid") Integer chatroomId) {
        chatRoomService.deleteChatroom(chatroomId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{chatroomid}/messages")
    @Operation(summary = "채팅룸 내역 가져오기", description = "특정 채팅룸의 채팅 내역을 가져옵니다.")
    public ResponseEntity<?> getChatMessages(@PathVariable("chatroomid") Integer chatroomId) {
        ChatRoomMessageResponse response = chatRoomService.getChatMessages(chatroomId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{chatroomid}")
    @Operation(summary = "특정 채팅방 상세 정보 가져오기", description = "특정 채팅방의 상세 정보를 반환합니다.")
    public ResponseEntity<?> getChatRomDetailInChat(@PathVariable("chatroomid") int chatroomId) {
        ChatRoomDetailInChatResponse response = chatRoomService.getChatRoomDetailInChat(chatroomId);
        return ResponseEntity.ok(response);
    }
}
