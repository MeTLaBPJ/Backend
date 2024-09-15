package com.metlab_project.backend.controller.user;

import com.metlab_project.backend.domain.dto.user.UserInfoResponse;
import com.metlab_project.backend.service.user.UserService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/info")
    @Operation(summary = "유저 마이페이지 정보 불러오기", description = "유저가 설정한 마이페이지에 등록될 정보를 불러옵니다.")
    public ResponseEntity<UserInfoResponse> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String schoolEmail = authentication.getName(); // JWT에서 가져온 이메일

        UserInfoResponse userInfo = userService.getUserInfoBySchoolEmail(schoolEmail);
        return ResponseEntity.ok(userInfo);
    }

    @PutMapping("/update")
    @Operation(summary = "유저 마이페이지 정보 수정", description = "유저의 마이페이지에 존재하는 정보를 수정합니다.")
    public ResponseEntity<UserInfoResponse> updateUserInfo(@RequestBody UserInfoResponse updatedUserInfo) {
        // JWT로부터 현재 유저의 이메일을 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String schoolEmail = authentication.getName();

        UserInfoResponse updatedUser = userService.updateUserInfoBySchoolEmail(schoolEmail, updatedUserInfo);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/info/{nickname}/{chatRoomId}") 
    @Operation(summary = "참가중인 채팅룸 속 다른 유저 프로필 불러오기", description = "유저가 참가중인 채팅룸 속 다른 유저의 마이페이지 정보를 확인합니다.")
    public ResponseEntity<UserInfoResponse> getAnotherUserInfo(@PathVariable String nickname, @RequestParam Long chatRoomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        // 해당 채팅방에 속해 있는지 확인
        boolean isAuthorized = userService.isUserInChatRoom(currentUserEmail, nickname, chatRoomId);
        if (!isAuthorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // 같은 채팅방일 경우 유저 정보 반환
        UserInfoResponse targetUserInfo = userService.getAnotherUserInfoByNickname(nickname);
        return new ResponseEntity<>(targetUserInfo, HttpStatus.OK);  
    }

    @DeleteMapping("/delete")
    @Operation(summary = "회원 탈퇴", description = "유저의 계정을 삭제합니다.")
    public ResponseEntity<String> deletUserInfo(){
        Autentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String schoolEmail = authentication.getName();

        userService.deleteUserInfoBySchoolEmail(schoolEmail);

        return new ResponseEntity<>("User deletd successfully", HttpStatus.OK);
    } 
}