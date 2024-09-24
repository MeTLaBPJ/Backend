package com.metlab_project.backend.controller.user;

import com.metlab_project.backend.domain.dto.user.res.UserInfoResponse;
import com.metlab_project.backend.domain.dto.user.req.UserInfoRequest;
import com.metlab_project.backend.domain.entity.user.CustomUserDetails;
import com.metlab_project.backend.service.user.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 유저 정보 가져옴
    @GetMapping()
    public UserInfoResponse getUserInfo() {
        // 인증된 사용자의 정보를 SecurityContextHolder에서 가져옴
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String schoolEmail = userDetails.getUser().getSchoolEmail();

        return userService.getUserInfoBySchoolEmail(schoolEmail);
    }
        // 유저 정보 수정
    @PutMapping()
    public UserInfoResponse updateUserInfo(@RequestBody UserInfoRequest updatedUserInfo) {
        // 인증된 사용자의 정보를 SecurityContextHolder에서 가져옴
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String schoolEmail = userDetails.getUser().getSchoolEmail();

        return userService.updateUserInfo(schoolEmail, updatedUserInfo);
    }

}