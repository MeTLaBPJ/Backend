package com.metlab_project.backend.controller.chatroom;

import com.metlab_project.backend.domain.dto.chatroom.req.ChatroomCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
public class ChatRoomController {
    @GetMapping("/list")
    @Operation(summary = "STATUS가 WAITING인 채팅룸 불러오기", description = "STATUS가 WAITING인 채팅룸을 불러옵니다.")
    public ResponseEntity<?> getAllChatroom(){
        //Status Waiting인 채팅룸 불러오는건데, /list-my에서 넘겨줄 본인이 속한 채팅방 제외하고 불러오면됨.
        return ResponseEntity.ok(true);
    }

    @GetMapping("/list-my")
    @Operation(summary = "유저가 참가중인 채팅룸 불러오기", description = "유저가 참가중인 채팅룸을 불러옵니다.")
    public ResponseEntity<?> getMyChatroom(){
        //본인이 속한 채팅룸(Status 상관없이 불러오면 됨)
        return ResponseEntity.ok(true);
    }

    @GetMapping("/participants/{chatroomid}")
    @Operation(summary = "채팅룸 참여 유저 불러오기", description = "특정 채팅룸의 참여자 목록을 불러옵니다.")
    public ResponseEntity<?> getParticipants(@PathVariable("chatroomid") String chatroomId){
        return ResponseEntity.ok(true);

    }

    @GetMapping("/summary/{chatroomid}")
    @Operation(summary = "채팅룸 요약 불러오기", description = "특정 채팅룸의 요약 정보를 불러옵니다.")
    public ResponseEntity<?> getChatroomSummary(@PathVariable("chatroomid") String chatroomId){
        return ResponseEntity.ok(true);
    }

    @PostMapping
    @Operation(summary = "채팅룸 생성하기", description = "채팅룸을 생성합니다.")
    public ResponseEntity createChatroom(@Valid @RequestBody ChatroomCreateRequest request){
        return ResponseEntity.ok(true);
    }
}
