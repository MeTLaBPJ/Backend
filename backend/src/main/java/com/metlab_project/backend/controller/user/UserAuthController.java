package com.metlab_project.backend.controller.user;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.metlab_project.backend.domain.dto.user.req.LoginRequestDto;
import com.metlab_project.backend.domain.dto.user.req.UserJoinRequestDto;
import com.metlab_project.backend.service.email.EmailService;
import com.metlab_project.backend.service.user.JoinService;
import com.metlab_project.backend.service.user.ReissueService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    private final JoinService joinService;
    private final ReissueService reissueService;
    private final EmailService emailService;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "CustomLoginFilter 처리 후 결과를 반환합니다.")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "CustomLogoutFilter 처리 후 결과를 반환합니다.")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/sign-up/email")
    @Operation(summary = "회원가입 시 메일 전송", description = "인증 코드가 담긴 메일을 전송합니다.")
    public ResponseEntity<?> mailConfirm(@RequestParam("email") String email) throws Exception {
        return ResponseEntity.ok(emailService.sendSimpleMessage(email));
    }


    @PostMapping("/sign-up/email/check")
    @Operation(summary = "인증 코드 검증", description = "인증 코드를 검증합니다.")
    public ResponseEntity<?> mailConfirmCheck(@RequestBody Map<String, String> request) throws Exception {
        String email = request.get("email");
        String code = request.get("key");
        return ResponseEntity.ok(joinService.confirmMailCode(email, code));
    }

    @GetMapping("/isExist/{nickname}")
    @Operation(summary = "닉네임 중복 체크", description = "닉네임의 중복 여부를 체크합니다")
    public ResponseEntity<?> checkNickname(@PathVariable("nickname") String nickname){
        return ResponseEntity.ok(joinService.checkNickname(nickname));
    }

    @PostMapping("/join")
    @Operation(summary = "회원가입", description = "사용자 입력 정보를 기반으로 사용자 계정을 생성합니다.")
    public ResponseEntity<?> joinProcess(@Valid @RequestBody UserJoinRequestDto userJoinRequestDto) {
        joinService.joinProcess(userJoinRequestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        return reissueService.reissueRefreshToken(request, response);
    }
}