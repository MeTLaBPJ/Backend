package com.metlab_project.backend.controller.chatroom;
import com.metlab_project.backend.domain.dto.chatroom.ChatroomCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
public class ChatroomController {

    @GetMapping("/list")
    @Operation(summary="WAITING 상태인 채팅룸 목록 불러오기", description="WAITING 상태인 채팅룸의 목록을 불러옵니다.")
    public ResponseEntity<?> getAllChatroomList() {
        try {

        } catch () {
        }
    }

    @GetMapping("/list/{schoolEmail}")
    @Operation(summary="유저가 참여하고 있는 채팅룸 목록 불러오기", description="유저가 참여하고 있는 채팅룸의 목록을 불러옵니다.")
    public ResponseEntity<?> getChatroomList(@PathVariable String schoolEmail){
        try{

        }catch(){

        }
    }

    @GetMapping("/{chatroomid}/participant")
    @Operation(summary="특정 채팅룸의 참여자 목록 불러오기", description="특정 채팅룸의 참여자 목록을 불러옵니다.")
    public ResponseEntity<?> getChatroomParticipant(@PathVariable("chatroomid") String chatroomId){
        try{

        }catch(){

        }
    }

    @PostMapping
    @Operation(summary="채팅룸 생성하기", description="채팅룸을 생성합니다.")
    public ResponseEntity<?> createChatroom(@RequestBody ChatroomCreateRequest request){
        try{

        }catch(){

        }
    }

    @PostMapping("/activate")
    @Operation(summary="채팅룸 활성화 하기", description="채팅룸의 상태를 active로 전환합니다.")
    public ResponseEntity<?> activeChatroom(){
        try{

        }catch(){

        }
    }
}
