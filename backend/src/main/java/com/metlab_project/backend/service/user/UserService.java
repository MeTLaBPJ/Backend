package com.metlab_project.backend.service.user;

import com.metlab_project.backend.domain.dto.user.res.UserInfoResponse;
import com.metlab_project.backend.domain.dto.user.req.UserInfoRequest;
import com.metlab_project.backend.domain.entity.user.CustomUserDetails;
import com.metlab_project.backend.domain.entity.user.User;
import com.metlab_project.backend.repository.user.UserRepository;
import com.metlab_project.backend.domain.dto.user.res.MyPageResponseDto;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService{
    private final UserRepository userRepository;

    public UserInfoResponse getUserInfoBySchoolEmail(String schoolEmail){
        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid Email"));

        return UserInfoResponse.builder()
                .schoolEmail(user.getSchoolEmail())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .studentId(user.getStudentId())
                .college(user.getCollege())
                .department(user.getDepartment())
                .mbti(user.getMbti() != null ? user.getMbti() : "Unknown")
                .smoking(user.getSmoking())
                .drinking(user.getDrinking())
                .height(user.getHeight())
                .shortIntroduce(user.getShortIntroduce())
                .profile(user.getProfile())
                .build();

    }

  @Override
  @Transactional
public UserDetails loadUserByUsername(String schoolEmail) throws UsernameNotFoundException {
    User user = userRepository.findBySchoolEmail(schoolEmail)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + schoolEmail));
    return new CustomUserDetails(user);
}

    public UserInfoResponse updateUserInfo(String schoolEmail, UserInfoRequest updatedUserInfo) {
        System.out.println("Received updatedUserInfo: " + updatedUserInfo);

        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new EntityNotFoundException("Invalid Email: " + schoolEmail));

        // 필드 유효성 검사
        if (updatedUserInfo.getNickname() == null || updatedUserInfo.getNickname().isEmpty()) {
                throw new IllegalArgumentException("Nickname cannot be empty");
        }

        // 유저 정보 업데이트
        user.setNickname(updatedUserInfo.getNickname());
        user.setMbti(updatedUserInfo.getMbti());
        user.setHeight(updatedUserInfo.getHeight());
        user.setDrinking(updatedUserInfo.getDrinking());
        user.setSmoking(updatedUserInfo.getSmoking());
        user.setShortIntroduce(updatedUserInfo.getShortIntroduce());
        user.setProfile(updatedUserInfo.getProfile());
        
       
        // 변경 사항을 저장
        userRepository.save(user);

        
        
        // 업데이트된 정보 반환
        return UserInfoResponse.builder()
                .schoolEmail(user.getSchoolEmail())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .studentId(user.getStudentId())
                .college(user.getCollege())
                .department(user.getDepartment())
                .mbti(user.getMbti() != null ? user.getMbti() : "Unknown")
                .height(user.getHeight())
                .smoking(user.getSmoking())
                .drinking(user.getDrinking())
                .shortIntroduce(user.getShortIntroduce())
                .profile(user.getProfile())
                .build();
    }
 

    public String getRefreshToken(String schoolEmail){
        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid Email"));

        return user.getRefreshtoken();
    }
}
