package com.metlab_project.backend.controller.chatroom;

import com.metlab_project.backend.domain.dto.chatroom.ChatroomCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
public class ChatRoomController {

    @GetMapping("/list")
    @Operation(summary = "WAITING 상태인 채팅룸 목록 불러오기", description = "WAITING 상태인 채팅룸의 목록을 불러옵니다.")
    public ResponseEntity<?> getAllChatroomList() {
        try {
            // TODO: 채팅룸 목록을 불러오는 로직을 구현하세요.
        } catch (Exception e) {
            // TODO: 예외 처리 로직을 구현하세요.
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
        return ResponseEntity.ok("채팅룸 목록"); // TODO: 실제 데이터를 반환하도록 수정하세요.
    }

    @GetMapping("/list/{schoolEmail}")
    @Operation(summary = "유저가 참여하고 있는 채팅룸 목록 불러오기", description = "유저가 참여하고 있는 채팅룸의 목록을 불러옵니다.")
    public ResponseEntity<?> getChatroomList(@PathVariable String schoolEmail) {
        try {
            // TODO: 유저가 참여하고 있는 채팅룸 목록을 불러오는 로직을 구현하세요.
        } catch (Exception e) {
            // TODO: 예외 처리 로직을 구현하세요.
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
        return ResponseEntity.ok("참여 중인 채팅룸 목록"); // TODO: 실제 데이터를 반환하도록 수정하세요.
    }

    @GetMapping("/{chatroomid}/participant")
    @Operation(summary = "특정 채팅룸의 참여자 목록 불러오기", description = "특정 채팅룸의 참여자 목록을 불러옵니다.")
    public ResponseEntity<?> getChatroomParticipant(@PathVariable("chatroomid") String chatroomId) {
        try {
            // TODO: 특정 채팅룸의 참여자 목록을 불러오는 로직을 구현하세요.
        } catch (Exception e) {
            // TODO: 예외 처리 로직을 구현하세요.
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
        return ResponseEntity.ok("채팅룸 참여자 목록"); // TODO: 실제 데이터를 반환하도록 수정하세요.
    }

    @PostMapping
    @Operation(summary = "채팅룸 생성하기", description = "채팅룸을 생성합니다.")
    public ResponseEntity<?> createChatroom(@RequestBody ChatroomCreateRequest request) {
        try {
            // TODO: 채팅룸을 생성하는 로직을 구현하세요.
        } catch (Exception e) {
            // TODO: 예외 처리 로직을 구현하세요.
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
        return ResponseEntity.ok("채팅룸 생성 완료"); // TODO: 실제 데이터를 반환하도록 수정하세요.
    }

    @PostMapping("/activate")
    @Operation(summary = "채팅룸 활성화 하기", description = "채팅룸의 상태를 active로 전환합니다.")
    public ResponseEntity<?> activeChatroom() {
        try {
            // TODO: 채팅룸을 활성화하는 로직을 구현하세요.
        } catch (Exception e) {
            // TODO: 예외 처리 로직을 구현하세요.
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
        return ResponseEntity.ok("채팅룸 활성화 완료"); // TODO: 실제 데이터를 반환하도록 수정하세요.
    }
}
