package com.metlab_project.backend.domain.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_information")
public class UserInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 30)
    private String shortIntroduce;

    @Column(length = 10)
    private String mbti;

    @Column(length = 10)
    private String height;

    @Column(length = 20)
    private String drinking;

    @Column(length = 20)
    private String smoking;

    @Column(length = 2000)
    private String profileImage;
}