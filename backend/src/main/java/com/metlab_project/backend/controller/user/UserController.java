package com.metlab_project.backend.controller.user;

import com.metlab_project.backend.domain.dto.user.UserInfoResponse;
import com.metlab_project.backend.security.jwt.JwtTokenProvider;
import com.metlab_project.backend.service.user.UserService;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    // 현재 유저의 이메일을 가져옴
    private String getUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    
    @GetMapping("/info")
    @Operation(summary = "유저 마이페이지 정보 불러오기", description = "유저가 설정한 마이페이지에 등록될 정보를 불러옵니다.")
    public ResponseEntity<UserInfoResponse> getUserInfo() {
        String schoolEmail = getUserEmail();

        UserInfoResponse userInfo = userService.getUserInfoBySchoolEmail(schoolEmail);
        return ResponseEntity.ok(userInfo);
    }

    @PutMapping("/update")
    @Operation(summary = "유저 마이페이지 정보 수정", description = "유저의 마이페이지에 존재하는 정보를 수정합니다.")
    public ResponseEntity<UserInfoResponse> updateUserInfo(@RequestBody UserInfoResponse updatedUserInfo) {
        String schoolEmail = getUserEmail();

        UserInfoResponse updatedUser = userService.updateUserInfoBySchoolEmail(schoolEmail, updatedUserInfo);
        return ResponseEntity.ok(updatedUser);
    }

<<<<<<< HEAD
    @GetMapping("/info/{nickname}/{chatRoomId}") 
    @Operation(summary = "참가중인 채팅룸 속 다른 유저 프로필 불러오기", description = "유저가 참가중인 채팅룸 속 다른 유저의 마이페이지 정보를 확인합니다.")
    public ResponseEntity<UserInfoResponse> getAnotherUserInfo(@PathVariable String nickname, @RequestParam Long chatRoomId) {
        String schoolEmail = getUserEmail();

        // 해당 채팅방에 속해 있는지 확인
        userService.checkUserInChatRoom(schoolEmail, nickname, chatRoomId);

        // 같은 채팅방일 경우 유저 정보 반환
        UserInfoResponse targetUserInfo = userService.getAnotherUserInfoByNickname(nickname);
        return new ResponseEntity<>(targetUserInfo, HttpStatus.OK);  
    }

    @DeleteMapping("/delete")
    @Operation(summary = "회원 탈퇴", description = "유저의 계정을 삭제합니다.")
    public ResponseEntity<String> deleteUserInfo(){
        String schoolEmail = getUserEmail();

        userService.deleteUserInfoBySchoolEmail(schoolEmail);

        return new ResponseEntity<>("User account deleted successfully.", HttpStatus.OK);
    } 
=======
>>>>>>> develop
}