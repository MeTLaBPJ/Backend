package com.metlab_project.backend.security;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metlab_project.backend.domain.dto.user.res.UserInfoResponse;
import com.metlab_project.backend.domain.entity.jwt.RefreshEntity;
import com.metlab_project.backend.domain.entity.user.User;
import com.metlab_project.backend.repository.jwt.RefreshTokenRepository;
import com.metlab_project.backend.repository.user.UserRepository;
import com.metlab_project.backend.security.jwt.JwtTokenProvider;
import com.metlab_project.backend.service.user.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CustomLoginFilter(AuthenticationManager authenticationManager,
                             RefreshTokenRepository refreshTokenRepository,
                             JwtTokenProvider jwtTokenProvider,
                             UserService userService,
                             UserRepository userRepository) {
        super(authenticationManager);
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.userRepository = userRepository;
        setFilterProcessesUrl("/api/users/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            log.debug("Attempting authentication for request: {}", request.getRequestURI());
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            String schoolEmail = loginRequest.getSchoolEmail();
            String password = loginRequest.getPassword();

            if (schoolEmail == null || password == null) {
                log.warn("Login attempt with null email or password");
                throw new AuthenticationException("Email or password is missing") {};
            }

            log.info("Login attempt for email: {}", schoolEmail);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(schoolEmail, password);
            return this.getAuthenticationManager().authenticate(authToken);
        } catch (IOException e) {
            log.error("Failed to parse authentication request", e);
            throw new AuthenticationException("Failed to parse authentication request") {};
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        log.info("Successful authentication for user: {}", authentication.getName());

        String schoolEmail = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        try {
            UserInfoResponse userInfo = userService.getUserInfoBySchoolEmail(schoolEmail);

            String accessToken = jwtTokenProvider.generateAccessToken(userInfo.getSchoolEmail());
            String refreshToken = jwtTokenProvider.generateRefreshToken(schoolEmail);

            log.debug("Generated access token: {}", accessToken);
            log.debug("Generated refresh token: {}", refreshToken);

            addRefreshEntity(schoolEmail, refreshToken, jwtTokenProvider.getExpirationDateFromToken(refreshToken).getTime());

            response.addHeader("Authorization", "Bearer " + accessToken);
            response.addCookie(createCookie("refresh", refreshToken, jwtTokenProvider.getExpirationDateFromToken(refreshToken).getTime()));
            response.setStatus(HttpServletResponse.SC_OK);

            UserInfoResponse responseBody = new UserInfoResponse(userInfo.getSchoolEmail(), userInfo.getNickname(), userInfo.getRole());
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
            response.setContentType("application/json");
        } catch (Exception e) {
            log.error("Error during successful authentication process", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred during the login process");
        }
    }

    private void addRefreshEntity(String schoolEmail, String refresh, Long refreshExpireTime) {
        try {
            refreshTokenRepository.findByUser_SchoolEmail(schoolEmail).ifPresent(refreshTokenRepository::delete);

            User user = userRepository.findBySchoolEmail(schoolEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + schoolEmail));

            RefreshEntity refreshEntity = RefreshEntity.builder()
                    .user(user)
                    .token(refresh)
                    .expiration(refreshExpireTime)
                    .build();

            refreshTokenRepository.save(refreshEntity);
            log.debug("Refresh token saved for user: {}", schoolEmail);
        } catch (Exception e) {
            log.error("Error saving refresh token for user: {}", schoolEmail, e);
        }
    }

    private Cookie createCookie(String key, String value, Long refreshExpireTime) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(Math.toIntExact(refreshExpireTime / 1000));
        cookie.setPath("/");
        return cookie;
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.warn("Unsuccessful authentication attempt", failed);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse("Authentication failed: " + failed.getMessage())));
        response.setContentType("application/json");
    }

    private static class LoginRequest {
        private String schoolEmail;
        private String password;

        public String getSchoolEmail() { return schoolEmail; }
        public void setSchoolEmail(String schoolEmail) { this.schoolEmail = schoolEmail; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    private static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}