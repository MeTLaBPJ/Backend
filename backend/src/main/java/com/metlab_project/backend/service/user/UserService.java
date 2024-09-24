package com.metlab_project.backend.service.user;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.metlab_project.backend.domain.dto.user.req.UserInfoRequest;
import com.metlab_project.backend.domain.dto.user.res.UserInfoResponse;
import com.metlab_project.backend.domain.entity.ChatRoom;
import com.metlab_project.backend.domain.entity.user.CustomUserDetails;
import com.metlab_project.backend.domain.entity.user.User;
import com.metlab_project.backend.domain.entity.user.UserInformation;
import com.metlab_project.backend.exception.CustomErrorCode;
import com.metlab_project.backend.exception.CustomException;
import com.metlab_project.backend.repository.chatroom.ChatRoomRepository;
import com.metlab_project.backend.repository.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String schoolEmail) throws UsernameNotFoundException {
        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + schoolEmail));
        return new CustomUserDetails(user);
    }

    public UserInfoResponse getUserInfoBySchoolEmail(String schoolEmail) {
        validateUserAccess(schoolEmail);

        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND, "User with Email " + schoolEmail + " not found"));

        UserInformation userInformation = user.getUserInformation();
        if (userInformation == null) {
            throw new CustomException(CustomErrorCode.USER_NOT_FOUND, "User information not found for email " + schoolEmail);
        }

        return buildUserInfoResponse(user, userInformation);
    }

    public UserInfoResponse getUserInfoBySchoolEmail() {
        String schoolEmail = getUserEmail();
        return getUserInfoBySchoolEmail(schoolEmail);
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
        return buildUserInfoResponse(user, user.getUserInformation());
    }

    public UserInfoResponse updateUserDetail(String schoolEmail, UserInfoResponse updatedUserInfo) {
        validateUserAccess(schoolEmail);

        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND, "User with Email " + schoolEmail + " not found"));

        UserInformation userInformation = user.getUserInformation();
        if (userInformation == null) {
            throw new CustomException(CustomErrorCode.USER_NOT_FOUND, "User information with Email " + schoolEmail + " not found");
        }

        user.setNickname(updatedUserInfo.getNickname());
        userInformation.setMbti(updatedUserInfo.getMbti());
        userInformation.setProfileImage(updatedUserInfo.getProfile());
        userInformation.setShortIntroduce(updatedUserInfo.getShortIntroduce());
        userInformation.setHeight(updatedUserInfo.getHeight());
        userInformation.setDrinking(updatedUserInfo.getDrinking());
        userInformation.setSmoking(updatedUserInfo.getSmoking());

        userRepository.save(user);

        return buildUserInfoResponse(user, userInformation);
    }

    public UserInfoResponse getAnotherUserDetail(String nickname, Integer chatRoomId) {
        String schoolEmail = getUserEmail();
        validateUserAccess(schoolEmail);

        checkUserInChatRoom(schoolEmail, nickname, chatRoomId);

        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND, "User with Nickname " + nickname + " not found"));

        UserInformation userInformation = user.getUserInformation();
        if (userInformation == null) {
            throw new CustomException(CustomErrorCode.USER_NOT_FOUND, "User information with Nickname " + nickname + " not found");
        }

        return buildUserInfoResponse(user, userInformation);
    }

    public void deleteUserInfo() {
        String schoolEmail = getUserEmail();
        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND, "User with Email " + schoolEmail + " not found"));

        userRepository.delete(user);
    }

    public String getRefreshToken(String schoolEmail) {
        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid Email"));

        return user.getRefreshtoken();
    }

    private void validateUserAccess(String schoolEmail) {
        String userEmail = getUserEmail();
        if (!userEmail.equals(schoolEmail)) {
            throw new CustomException(CustomErrorCode.FORBIDDEN_ACCESS_TO_OTHER_USER_INFO, "Access denied for user " + schoolEmail);
        }
    }

    private String getUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private UserInfoResponse buildUserInfoResponse(User user, UserInformation userInformation) {
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
                .profile(userInformation.getProfileImage())
                .build();
    }

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

        if (!isRequestingUserInRoom || !isTargetUserInRoom) {
            throw new CustomException(CustomErrorCode.FORBIDDEN_ACCESS_TO_OTHER_USER_INFO, "Only users' information in the same chat room is accessible");
        }
    }
}