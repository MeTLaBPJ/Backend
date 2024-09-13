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

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 유저 정보 가져옴
    @GetMapping("/{schoolEmail}")
    public UserInfoResponse getUserInfo(@PathVariable String schoolEmail) {
        return userService.getUserInfoBySchoolEmail(schoolEmail);
    }


    // 유저 정보 수정
    @PutMapping("/{schoolEmail}")
    public UserInfoResponse updateUserInfo(@PathVariable String schoolEmail, @RequestBody UserInfoResponse updatedUserInfo) {
        return userService.updateUserInfo(schoolEmail, updatedUserInfo);
    }
}