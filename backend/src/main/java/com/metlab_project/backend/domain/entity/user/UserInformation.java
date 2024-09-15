package com.metlab_project.backend.domain.entity;

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
@Table(name = "UserInformation")
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