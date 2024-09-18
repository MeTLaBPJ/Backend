package com.metlab_project.backend.domain.entity.email;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Getter
@NoArgsConstructor
@Table(name = "email_auth")
@Entity
public class EmailAuth {
    @Id
    @Column(name = "email_key", nullable = false)
    private String key;

    @Column(name = "email", nullable = false)
    private String email;

    public EmailAuth(String key, String email) {
        this.key = key;
        this.email = email;
    }
}