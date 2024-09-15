package com.metlab_project.backend.domain.entity;

import com.metlab_project.backend.domain.entity.user.User;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type")
    private MessageType type;

    @Column(name = "school_Email", nullable = false)
    private String schoolEmail;

    @Column(name = "chatroom_id", nullable = false)
    private Integer chatroomId;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "school_Email", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "chatroom_id", insertable = false, updatable = false)
    private ChatRoom chatRoom;

    private enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        START
    }
}
