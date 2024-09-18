package com.metlab_project.backend.repository.chatroom;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.metlab_project.backend.domain.entity.ChatRoom;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    List<ChatRoom> findByStatus(ChatRoom.Status status);
    List<ChatRoom> findByIdIn(List<Integer> ids);
}
