package com.metlab_project.backend.domain.entity.user;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class CustomUserDetails implements UserDetails {
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        log.debug("Getting authorities for user: {}", user.getSchoolEmail());
        return Collections.singletonList(new SimpleGrantedAuthority(UserRole.ROLE_USER.toString()));
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