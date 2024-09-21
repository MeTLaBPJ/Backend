package com.metlab_project.backend.controller.user;

import com.metlab_project.backend.domain.dto.user.req.LoginRequestDto;
import com.metlab_project.backend.domain.dto.user.req.UserJoinRequestDto;
import com.metlab_project.backend.service.user.JoinService;
import com.metlab_project.backend.service.user.ReissueService;
import com.metlab_project.backend.service.email.EmailService;

import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@Slf4j
@RestController
public class UserAuthController {

    private final JoinService joinService;
    private final ReissueService reissueService;
    private final EmailService emailService;
    
    @PostMapping("/api/users/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/users/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "유저 회원가입시 이메일 인증번호 전송", notes = "유저 회원가입시 이메일 인증번호 전송")
    @PostMapping("/sign-up/email")
    public ResponseEntity<?> mailConfirm(@RequestParam("email") String email) throws Exception {
        return ResponseEntity.ok(emailService.sendSimpleMessage(email));
    }
    

    @PostMapping("/sign-up/email/check")
public ResponseEntity<?> mailConfirmCheck(@RequestBody Map<String, String> request) throws Exception {
    String email = request.get("email");
    String code = request.get("key"); // "key"를 code로 사용

    // 인증 코드 확인 로직
    return ResponseEntity.ok(joinService.confirmMailCode(email, code));
}

    @GetMapping("isExist/{nickname}")
    public ResponseEntity<?> checkNickname(@PathVariable("nickname") String nickname)
    {
        return ResponseEntity.ok(joinService.checkNickname(nickname));
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
