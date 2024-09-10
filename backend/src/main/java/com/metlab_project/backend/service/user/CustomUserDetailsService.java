package com.metlab_project.backend.service.user;

import com.metlab_project.backend.domain.dto.user.CustomUserDetails;
import com.metlab_project.backend.domain.entity.User;
import com.metlab_project.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = userRepository.findBySchoolEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Find User by Username(SchoolEmail) is failed" + username));

        return new CustomUserDetails(user);
    }
}