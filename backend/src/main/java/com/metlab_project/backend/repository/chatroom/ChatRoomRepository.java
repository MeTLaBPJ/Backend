package com.metlab_project.backend.repository.chatroom;

import com.metlab_project.backend.domain.entity.ChatRoom;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    List<ChatRoom> findByStatus(ChatRoom.Status status);
    List<ChatRoom> findByIdIn(List<Integer> ids);
}
