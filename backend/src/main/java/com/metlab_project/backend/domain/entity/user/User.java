package com.metlab_project.backend.domain.entity.user;

import com.metlab_project.backend.domain.entity.ChatRoom;
import com.metlab_project.backend.domain.entity.Message;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "school_email", nullable = false, length = 30, unique = true)
    private String schoolEmail;

    @Column(nullable = false, length = 255)
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

    @Column(length = 30)
    private String height;

    @Column(length = 30)
    private String drinking;

    @Column(length = 30)
    private String smoking;

    @Column(length = 30)
    private String shortIntroduce;



    private String blacklist;

    private String refreshtoken;

    private Boolean isblocked;


    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private UserRole role;

    @ManyToMany
    @Builder.Default
    @JoinTable(
            name = "user_chatroom",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "chatroom_id")
    )
    private List<ChatRoom> chatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Message> messages;

    public void addChatRoom(ChatRoom chatRoom) {
        this.chatRooms.add(chatRoom);
        chatRoom.getUsers().add(this);
    }

    public void removeChatRoom(ChatRoom chatRoom) {
        this.chatRooms.remove(chatRoom);
        chatRoom.getUsers().remove(this);
    }
}