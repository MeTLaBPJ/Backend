package com.metlab_project.backend.service.user;

import com.metlab_project.backend.domain.dto.user.UserInfoResponse;
import com.metlab_project.backend.domain.entity.User;
import com.metlab_project.backend.domain.entity.UserInformation;
import com.metlab_project.backend.repository.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 현재 유저의 이메일을 가져옴
    private String getUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public UserInfoResponse getUserInfoBySchoolEmail(String schoolEmail){
        String userEmail = getUserEmail();
        if (!userEmail.equals(schoolEmail)) {    
                throw new AccessDeniedException("You can only view your own information."); // TODO 에러
        }

        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid Email")); // TODO 에러
        UserInformation userInformation = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid Email")); // TODO 에러

        return UserInfoResponse.builder()
                .schoolEmail(user.getSchoolEmail())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .studentId(user.getStudentId())
                .college(user.getCollege())
                .department(user.getDepartment())
                .mbti(user.getMbti() != null ? user.getMbti() : "Unknown")
                .shortIntroduce(userInformation.getShortIntroduce())
                .height(userInformation.getHeight())
                .drinking(userInformation.getDrinking())
                .smoking(userInformation.getSmoking())
                .profileImage(userInformation.getProfileImage())
                .build();
    }

    public UserInfoResponse updateUserInfoBySchoolEmail(String schoolEmail, UserInfoResponse updatedUserInfo) {
        String userEmail = getUserEmail();
        if (!userEmail.equals(schoolEmail)) {
            throw new AccessDeniedException("You can only update your own information."); // TODO 에러
        }

        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new EntityNotFoundException("Invalid Email: " + schoolEmail)); // TODO 에러
        UserInformation userInformation = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid Email")); // TODO 에러

        if (updatedUserInfo.getNickname() == null || updatedUserInfo.getNickname().isEmpty()) {
                throw new IllegalArgumentException("Nickname cannot be empty"); // TODO 에러
        }

        user.setNickname(updatedUserInfo.getNickname());
        userInformation.setMbti(updatedUserInfo.getMbti());
        userInformation.setProfileImage(updatedUserInfo.getProfileImage());
        userInformation.setShortIntroduce(updatedUserInfo.getShortIntroduce());
        userInformation.setHeight(updatedUserInfo.getHeight());
        userInformation.setDrinking(updatedUserInfo.getDrinking());
        userInformation.setSmoking(updatedUserInfo.getSmoking());

        userRepository.save(user);

        return UserInfoResponse.builder()
                .nickname(user.getNickname())
                .mbti(user.getMbti() != null ? user.getMbti() : "Unknown")
                .profileImage(userInformation.getProfileImage())
                .shortIntroduce(userInformation.getShortIntroduce())
                .height(userInformation.getHeight())
                .drinking(userInformation.getDrinking())
                .smoking(userInformation.getSmoking())
                .build();
    }

    public UserInfoResponse getAnotherUserInfoByNickname(String nickname){
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new BadCredentialsException("Invalid Nickname")); // TODO 에러
        UserInformation userInformation = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new BadCredentialsException("Invalid Nickname")); // TODO 에러

        return UserInfoResponse.builder()
                .schoolEmail(user.getSchoolEmail())
                .profileImage(userInformation.getProfileImage())
                .nickname(user.getNickname())
                .department(user.getDepartment())
                .studentId(user.getStudentId())
                .shortIntroduce(userInformation.getShortIntroduce())
                .mbti(user.getMbti() != null ? user.getMbti() : "Unknown")
                .height(userInformation.getHeight())
                .drinking(userInformation.getDrinking())
                .smoking(userInformation.getSmoking())
                .profileImage(userInformation.getProfileImage())
                .build();
    }

    public boolean isUserInChatRoom(String requestingUserEmail, String targetUserNickname, Long chatRoomId) {
        User requestingUser = userRepository.findBySchoolEmail(requestingUserEmail)
            .orElseThrow(() -> new EntityNotFoundException("Requesting user not found")); // TODO 에러
        User targetUser = userRepository.findByNickname(targetUserNickname)
            .orElseThrow(() -> new EntityNotFoundException("Target user not found")); // TODO 에러
        
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new EntityNotFoundException("Chat room not found")); // TODO 에러

        // 요청한 유저와 대상 유저가 모두 해당 채팅방에 속해 있는지 확인
        return chatRoom.getusers().contains(requestingUser) && 
               chatRoom.getusers().contains(targetUser);
    }

    public void deleteUserInfoBySchoolEmail(String schoolEmail){
        String userEmail = getUserEmail();
        if (!userEmail.equals(schoolEmail)) {
                throw new AccessDeniedException("You can only delete your own account.");
        }

        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new EntityNotFoundException("Invalid Email")); // TODO 에러
        
        userRepository.delete(user);
    }

    public String getRefreshToken(String schoolEmail){
        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid Email")); // TODO 에러

        return user.getRefreshtoken();
    }
}