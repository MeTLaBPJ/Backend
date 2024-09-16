package com.metlab_project.backend.domain.entity;

import com.metlab_project.backend.domain.entity.user.User;
import jakarta.persistence.*;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20)
    private MessageType type;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_email", referencedColumnName = "school_email")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", referencedColumnName = "id")
    private ChatRoom chatRoom;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        START
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}