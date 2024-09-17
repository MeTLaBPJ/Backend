package com.metlab_project.backend.controller.user;

import com.metlab_project.backend.domain.dto.user.req.LoginRequestDto;
import com.metlab_project.backend.domain.dto.user.req.UserJoinRequestDto;
import com.metlab_project.backend.service.user.JoinService;
import com.metlab_project.backend.service.user.ReissueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@Slf4j
@RestController
public class UserAuthController {

    private final JoinService joinService;
    private final ReissueService reissueService;
    
    @PostMapping("/api/users/login")
    public ResponseEntity<?> login(@RequestParam LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/users/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/users/join")
    public ResponseEntity<?> joinProcess(@Valid @RequestBody UserJoinRequestDto userJoinRequestDto) {

        log.info("[joinProcess] schoolEmail: {}", userJoinRequestDto.getSchoolEmail());
        // !! 기본적으로 ROLE_ADMIN 발급 !!
        joinService.joinProcess(userJoinRequestDto);

        return ResponseEntity.ok().build();
    }

     @PostMapping("/api/users/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        return reissueService.reissueRefreshToken(request, response);
    }

}
