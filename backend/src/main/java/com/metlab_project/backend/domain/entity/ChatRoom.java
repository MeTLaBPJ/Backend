package com.metlab_project.backend.domain.entity;

import com.metlab_project.backend.domain.entity.user.User;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    @Column(name = "host", nullable = false, length = 30)
    private String host;

    @Builder.Default
    @Column(name = "participant_male_count")
    private Integer participantMaleCount = 0;

    @Builder.Default
    @Column(name = "participant_female_count")
    private Integer participantFemaleCount = 0;

    private LocalDateTime deadline;

    @Column(length = 100)
    private String hashtags;

    @OneToMany(mappedBy = "chatRoom")
    private List<User> users;

    @OneToMany(mappedBy = "chatRoom")
    private List<Message> messages;

}