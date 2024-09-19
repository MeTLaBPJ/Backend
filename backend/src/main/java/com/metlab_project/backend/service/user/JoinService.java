package com.metlab_project.backend.service.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metlab_project.backend.domain.dto.user.req.UserJoinRequestDto;
import com.metlab_project.backend.domain.entity.user.User;
import com.metlab_project.backend.exception.EmailAuthNotEqualsException;
import com.metlab_project.backend.repository.email.EmailAuthRepository; // 추가된 임포트
import com.metlab_project.backend.repository.user.UserRepository;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class JoinService {
    private final UserRepository userRepository;
    private final EmailAuthRepository emailAuthRepository; // EmailAuthRepository 추가
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, EmailAuthRepository emailAuthRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.emailAuthRepository = emailAuthRepository; // 의존성 주입
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(UserJoinRequestDto userJoinRequestDto) {
        log.info("[joinProcess] joinDto = {}, {}, {}", userJoinRequestDto.getSchoolEmail(), userJoinRequestDto.getPassword(), userJoinRequestDto.getNickname());

        String schoolEmail = userJoinRequestDto.getSchoolEmail();
        String nickname = userJoinRequestDto.getNickname();
        String password = userJoinRequestDto.getPassword();

        if (userRepository.existsBySchoolEmail(schoolEmail)) {
            return;
        }

        User data = User.builder()
                .schoolEmail(schoolEmail)
                .password(bCryptPasswordEncoder.encode(password))
                .nickname(userJoinRequestDto.getNickname())
                .gender(userJoinRequestDto.getGender())
                .studentId(userJoinRequestDto.getStudentId())
                .college(userJoinRequestDto.getCollege())
                .department(userJoinRequestDto.getDepartment())
                .build();

        userRepository.save(data);
    }

    @Transactional(readOnly = true)
    public String confirmMailCode(String email, String code) {
        if (emailAuthRepository.existsByKey(code)) {
            return "인증번호가 확인되었습니다.";
        } else {
            throw new EmailAuthNotEqualsException(); // 적절한 예외 클래스 정의 필요
        }
    }

}
