package com.metlab_project.backend.service.message;

import com.metlab_project.backend.domain.entity.Message;
import com.metlab_project.backend.domain.entity.user.User;
import com.metlab_project.backend.repository.message.MessageRepository;
import com.metlab_project.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public Message handleJoinMessage(Integer chatroomId, Message message, String schoolEmail){
        Message saveMessage = settingMessage(message, chatroomId, schoolEmail);
        /**
         * 채팅룸 맴버 정보 업데이트 (구성원 리스트에 추가 + 인원 추가)
         * 사용자 채팅룸 입장기회 업데이트 (-1)
         * 사용자 채팅룸 리스트에 본인이 속한 채팅룸 아이디 추가
         */
    }

    public Message handleLeaveMessage(Integer chatroomId, Message message, String schoolEmail){
        Message saveMessage = settingMessage(message, chatroomId, schoolEmail);
        /**
         * 채팅룸 맴버 정보 업데이트 (구성원 리스트에서 삭제 + 인원 삭제)
         * 사용자 채팅룸 리스트에 본인이 속한 채팅룸 아이디 추가
         * 채팅룸이 엑티브면 퇴장메시지 전송
         */
    }

    public Message handleSendMessage(Integer chatroomId, Message message, String schoolEmail){
        Message saveMessage = settingMessage(message, chatroomId, schoolEmail);
        /**
         * 구독자들에게 메시지 전송
         */
    }

    public Message handleStartMessage(Integer chatroomId, Message message, String schoolEmail){
        Message saveMessage = settingMessage(message, chatroomId, schoolEmail);
        /**
         * 채팅룸 상태 active 상태로 전환
         * 채팅룸의 맴버가 충족 맴버에 도달했는지 체크
         */
    }

    public Message settingMessage(Message message, Integer chatroomId, String schoolEmail){
        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(null); // TODO 에러 핸들링
        String nickname = user.getNickname();

        message.setNickname(nickname);
        message.setSchoolEmail(schoolEmail);
        message.setChatroomId(chatroomId);

        return message;
    }
}
