package com.metlab_project.backend.domain.entity.user;

import com.metlab_project.backend.domain.entity.ChatRoom;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "school_email", nullable = false, length = 30)
    private String schoolEmail;

    @Column(nullable = false, length = 20)
    private String password;

    @Column(length = 20)
    private String nickname;

    @Builder.Default
    @Column(name = "ticket")
    private Integer tickets = 3;

    private LocalDate birthday;

    @Column(length = 10)
    private String gender;

    @Column(name = "student_id", length = 10)
    private String studentId;

    @Column(length = 30)
    private String college;

    @Column(length = 30)
    private String department;

    @Column(length = 10)
    private String mbti;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String blacklist;

    private String refreshtoken;

    private Boolean isblocked;

    private UserRole role;

    @ManyToMany
    @JoinTable(
            name = "user_chatroom",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "chatroom_id")
    )
    private List<ChatRoom> chatRooms = new ArrayList<>();

    public void addChatRoom(ChatRoom chatRoom) {
        this.chatRooms.add(chatRoom);
        chatRoom.getUsers().add(this);
    }

    public void removeChatRoom(ChatRoom chatRoom) {
        this.chatRooms.remove(chatRoom);
        chatRoom.getUsers().remove(this);
    }
}