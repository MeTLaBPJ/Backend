package com.metlab_project.backend.domain.entity.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.metlab_project.backend.domain.entity.ChatRoom;
import com.metlab_project.backend.domain.entity.Message;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Column(name = "tickets")
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

    @Column(name = "chatroom_id")
    private Integer chatroomId;

    @Column(length = 10)
    private String mbti;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 30)
    private String profile;

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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_information_id")
    private UserInformation userInformation;

    public void addChatRoom(ChatRoom chatRoom) {
        this.chatRooms.add(chatRoom);
        chatRoom.getUsers().add(this);
    }

    public void removeChatRoom(ChatRoom chatRoom) {
        this.chatRooms.remove(chatRoom);
        chatRoom.getUsers().remove(this);
    }
}