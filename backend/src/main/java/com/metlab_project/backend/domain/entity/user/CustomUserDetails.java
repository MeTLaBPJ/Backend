package com.metlab_project.backend.domain.entity.user;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class CustomUserDetails implements UserDetails {
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        // 역할이 null이 아닐 경우에만 추가
        if (user.getRole() != null) {
            collection.add((GrantedAuthority) () -> user.getRole().toString());
        } else {
            // 기본 역할 추가 또는 예외 처리
            collection.add((GrantedAuthority) () -> "ROLE_ANONYMOUS"); // 기본 역할 설정
        }

        return collection;

    }

    public User getUser() {
        return this.user;
    }

    @Override
    public String getUsername() {
        return this.user.getSchoolEmail();
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "CustomUserDetails{" +
                "schoolEmail='" + getUsername() + '\'' +
                ", authorities=" + getAuthorities() +
                '}';
    }
}