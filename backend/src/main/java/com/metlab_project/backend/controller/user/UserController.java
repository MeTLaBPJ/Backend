package com.metlab_project.backend.controller.user;

import com.metlab_project.backend.domain.dto.user.UserInfoResponse;
import com.metlab_project.backend.service.user.UserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 유저 정보 가져옴
    @GetMapping("/{schoolEmail}")
    public ResponseEntity<?> getUserInfo(@PathVariable String schoolEmail) {
        try {
            UserInfoResponse userInfo = userService.getUserInfoBySchoolEmail(schoolEmail);
            return new ResponseEntity<>(userInfo, HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    // 유저 정보 수정
    @PutMapping("/{schoolEmail}")
    public ResponseEntity<?> updateUserInfo(@PathVariable String schoolEmail, @RequestBody UserInfoResponse updatedUserInfo) {
        try {
            UserInfoResponse updatedUser = userService.updateUserInfo(schoolEmail, updatedUserInfo);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}