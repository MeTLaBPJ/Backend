package com.metlab_project.backend.security;

import com.metlab_project.backend.domain.entity.user.User;
import com.metlab_project.backend.repository.jwt.RefreshTokenRepository;
import com.metlab_project.backend.repository.user.UserRepository;
import com.metlab_project.backend.security.jwt.JwtTimeComponent;
import com.metlab_project.backend.security.jwt.JwtTokenProvider;
import com.metlab_project.backend.domain.entity.jwt.RefreshEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.Iterator;

@Slf4j
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTimeComponent jwtTimeComponent;
    private final UserRepository userRepository;

    public CustomLoginFilter(AuthenticationManager authenticationManager, RefreshTokenRepository refreshTokenRepository, JwtTokenProvider jwtTokenProvider, JwtTimeComponent jwtTimeComponent, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtTimeComponent = jwtTimeComponent;
        this.userRepository = userRepository;
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String schoolEmail = request.getParameter("schoolEmail");
        String password = request.getParameter("password");

        log.info("[attemptAuthentication] schoolEmail = {}", schoolEmail);
        log.info("[attemptAuthentication] password = {}", password);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(schoolEmail, password, null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        log.info("[successfulAuthentication] 로그인 성공");

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        final String schoolEmail = authentication.getName();
        final String role = auth.getAuthority();
        final String access = jwtTokenProvider.generateAccessToken(schoolEmail);
        final String refresh = jwtTokenProvider.generateRefreshToken(schoolEmail);

        log.info("[successfulAuthentication] username = {}", schoolEmail);
        log.info("[successfulAuthentication] access token = {}", access);
        log.info("[successfulAuthentication] refresh token = {}", refresh);

        addRefreshEntity(schoolEmail, refresh, jwtTimeComponent.getRefreshExpiration());

        response.addHeader("Authorization", "Bearer " + access);

        ResponseCookie refreshCookie = createRefreshTokenCookie(refresh, jwtTimeComponent.getRefreshExpiration());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        log.info("[successfulAuthentication] Set-Cookie header: {}", refreshCookie.toString());
        log.info("[successfulAuthentication] Response status: {}", response.getStatus());

        // 모든 응답 헤더 로깅
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            log.info("[successfulAuthentication] Response header - {}: {}", headerName, response.getHeader(headerName));
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void addRefreshEntity(String schoolEmail, String refresh, Long refreshExpireTime) {
        refreshTokenRepository.findByUser_SchoolEmail(schoolEmail).ifPresent(refreshTokenRepository::delete);

        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        RefreshEntity refreshEntity = RefreshEntity.builder()
                .token(refresh)
                .expiration(refreshExpireTime)
                .user(user)
                .build();

        refreshTokenRepository.save(refreshEntity);
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken, Long refreshExpireTime) {
        ResponseCookie cookie = ResponseCookie.from("refresh", refreshToken)
                .httpOnly(true)
                .secure(false)  // localhost에서는 false로 설정
                .path("/")
                .maxAge(refreshExpireTime / 1000)
                .sameSite("Lax")  // Strict 대신 Lax 사용
                .domain("localhost")  // localhost 명시
                .build();

        log.info("[createRefreshTokenCookie] Created cookie: {}", cookie.toString());
        return cookie;
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        log.info("[unsuccessfulAuthentication] 로그인 실패");
        response.setStatus(401);
    }
}