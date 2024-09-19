package com.metlab_project.backend.controller.user;

import com.metlab_project.backend.domain.dto.user.res.UserInfoResponse;
import com.metlab_project.backend.security.jwt.JwtTokenProvider;
import com.metlab_project.backend.service.user.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/info")
    @Operation(summary = "유저 마이페이지 정보 불러오기", description = "유저가 설정한 마이페이지에 등록될 정보를 불러옵니다.")
    public ResponseEntity<UserInfoResponse> getUserInfo() {

        UserInfoResponse response = userService.getUserInfoBySchoolEmail();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    @Operation(summary = "유저 마이페이지 정보 수정", description = "유저의 마이페이지에 존재하는 정보를 수정합니다.")
    public ResponseEntity<UserInfoResponse> updateUserInfo(@Valid @RequestBody UserInfoResponse request) {

        UserInfoResponse response = userService.updateUserInfoBySchoolEmail(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info/{nickname}/{chatRoomId}")
    @Operation(summary = "참가중인 채팅룸 속 다른 유저 프로필 불러오기", description = "유저가 참가중인 채팅룸 속 다른 유저의 마이페이지 정보를 확인합니다.")
    public ResponseEntity<UserInfoResponse> getAnotherUserInfo(@PathVariable String nickname, @RequestParam Integer chatRoomId) {
        // 같은 채팅방일 경우 유저 정보 반환
        UserInfoResponse response = userService.getAnotherUserInfoByNickname(nickname, chatRoomId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "회원 탈퇴", description = "유저의 계정을 삭제합니다.")
    public ResponseEntity<String> deleteUserInfo(){

        userService.deleteUserInfoBySchoolEmail();
        return ResponseEntity.ok("User account deleted successfully.");
    }
}