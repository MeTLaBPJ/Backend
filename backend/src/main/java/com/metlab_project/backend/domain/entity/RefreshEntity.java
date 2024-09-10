package com.metlab_project.backend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "refresh_tokens") // 테이블 이름을 지정할 수 있습니다.
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String schoolEmail;  // 유저의 이메일(고유)

    @Column(nullable = false)
    private String token;  // Refresh 토큰

    @Column(nullable = false)
    private Long expiration;  // 만료 시간 (초 단위로 저장)
}