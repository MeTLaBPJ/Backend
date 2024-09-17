package com.metlab_project.backend.service.user;

import com.metlab_project.backend.domain.dto.user.req.UserJoinRequestDto;
import com.metlab_project.backend.domain.entity.user.*;
import com.metlab_project.backend.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JoinService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
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
}
