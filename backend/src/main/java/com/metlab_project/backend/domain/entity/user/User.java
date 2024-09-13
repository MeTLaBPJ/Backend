package com.metlab_project.backend.domain.entity.user;

import com.metlab_project.backend.domain.entity.ChatRoom;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private Integer tickets = 1;

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

    @Column(name = "chatroom_id")
    private Integer chatroomId;

    private String blacklist;

    private String refreshtoken;

    private Boolean isblocked;

    private UserRole role;

    @ManyToOne
    @JoinColumn(name = "chatroom_id", insertable = false, updatable = false)
    private ChatRoom chatRoom;

}
