package com.metlab_project.backend.repository.message;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.metlab_project.backend.domain.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByChatroomIdAndTypeOrderByCreatedAtAsc(Integer chatroomId, Message.MessageType type);
}
