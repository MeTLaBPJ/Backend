package com.metlab_project.backend.domain.entity;

import com.metlab_project.backend.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ChatRoom")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "chatroom_name", nullable = false, length = 30)
    private String chatroomName;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "sub_title", length = 100)
    private String subTitle;

    @Column(name = "profile_image", length = 255)
    private String profileImage;

    @Column(name = "host", nullable = false, length = 30)
    private String host; // host's schoolEmail

    @Builder.Default
    @Column(name = "participant_male_count")
    private Integer participantMaleCount = 0;

    @Builder.Default
    @Column(name = "participant_female_count")
    private Integer participantFemaleCount = 0;

    @Column(name = "total_participant")
    private Integer totalParticipant;

    @Column(name = "max_members")
    private Integer maxMembers;

    @Column(name = "enter_check")
    private Boolean enterCheck;

    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", length = 20)
    private Status status = Status.WAITING;

    @Column(length = 100)
    private String hashtags;

    @Builder.Default
    @ManyToMany(mappedBy = "chatRooms")
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    public enum Status {
        WAITING,
        ACTIVE
    }

    public void addUser(User user) {
        this.users.add(user);
        user.getChatRooms().add(this);
    }

    public void removeUser(User user) {
        this.users.remove(user);
        user.getChatRooms().remove(this);
    }
}
