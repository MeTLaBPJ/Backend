package com.metlab_project.backend.service.user;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metlab_project.backend.domain.dto.user.res.UserInfoResponse;
import com.metlab_project.backend.domain.entity.ChatRoom;
import com.metlab_project.backend.domain.entity.user.User;
import com.metlab_project.backend.domain.entity.user.UserInformation;
import com.metlab_project.backend.exception.CustomErrorCode;
import com.metlab_project.backend.exception.CustomException;
import com.metlab_project.backend.repository.chatroom.ChatRoomRepository;
import com.metlab_project.backend.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

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

    // 유저 접근 권한 확인
    private void validateUserAccess(String schoolEmail) {
        String userEmail = getUserEmail();
        if (!userEmail.equals(schoolEmail)) {
            throw new CustomException(CustomErrorCode.FORBIDDEN_ACCESS_TO_OTHER_USER_INFO, "Access denied for user " + schoolEmail);
        }
    }

    // 유저 정보 가져옴
    public UserInfoResponse getUserInfoBySchoolEmail(String schoolEmail){
        validateUserAccess(schoolEmail);

        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND, "User with Email " + schoolEmail + " not found"));
        
        UserInformation userInformation = user.getUserInformation();
        if (userInformation == null) {
                throw new CustomException(CustomErrorCode.USER_NOT_FOUND, "User information not found for email " + schoolEmail);
        }

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

    // 유저 정보 수정
    public UserInfoResponse updateUserInfoBySchoolEmail(String schoolEmail, UserInfoResponse updatedUserInfo) {
        validateUserAccess(schoolEmail);

        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND, "User with Email " + schoolEmail + " not found"));
        
        UserInformation userInformation = user.getUserInformation();
        if (userInformation == null) {
                throw new CustomException(CustomErrorCode.USER_NOT_FOUND, "User information with Email " + schoolEmail + " not found");
        }

        if (updatedUserInfo.getNickname() == null || updatedUserInfo.getNickname().isEmpty()) {
                throw new IllegalArgumentException("Nickname cannot be empty");
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

    // 다른 유저 정보 가져옴
    public UserInfoResponse getAnotherUserInfoByNickname(String nickname){
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND, "User with Nickname " + nickname + " not found"));
        
        UserInformation userInformation = user.getUserInformation();
        if (userInformation == null) {
                throw new CustomException(CustomErrorCode.USER_NOT_FOUND, "User information with Nickname " + nickname + " not found");
        }

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

    // 동일한 채팅방에 접속 중인지 확인
    public void checkUserInChatRoom(String requestingUserEmail, String targetUserNickname, Integer chatRoomId) {
        User requestingUser = userRepository.findBySchoolEmail(requestingUserEmail)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND, "User with Email " + requestingUserEmail + " not found"));
        
        User targetUser = userRepository.findByNickname(targetUserNickname)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND, "User with nickname " + targetUserNickname + " not found"));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CHATROOM_NOT_FOUND, "Chat room with ID " + chatRoomId + " not found"));

        boolean isRequestingUserInRoom = requestingUser.getChatRooms().stream()
        .anyMatch(room -> room.getId().equals(chatRoomId));
        
        boolean isTargetUserInRoom = targetUser.getChatRooms().stream()
        .anyMatch(room -> room.getId().equals(chatRoomId));

        // 두 유저가 모두 해당 채팅방에 속해 있지 않으면 예외 발생
        if (!isRequestingUserInRoom || !isTargetUserInRoom) {
                throw new CustomException(CustomErrorCode.FORBIDDEN_ACCESS_TO_OTHER_USER_INFO, "Only users' information in the same chat room is accessible");
        }
    }

    // 유저 정보 삭제
    public void deleteUserInfoBySchoolEmail(String schoolEmail){
        validateUserAccess(schoolEmail);

        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND, "User with Email " + schoolEmail + " not found"));


        userRepository.delete(user);
    }

    public String getRefreshToken(String schoolEmail){
        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid Email"));

        return user.getRefreshToken();
    }
}