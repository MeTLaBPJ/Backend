package com.metlab_project.backend.security;

import com.metlab_project.backend.domain.dto.user.res.UserInfoResponse;
import com.metlab_project.backend.domain.entity.user.User;
import com.metlab_project.backend.repository.jwt.RefreshTokenRepository;
import com.metlab_project.backend.repository.user.UserRepository;
import com.metlab_project.backend.security.jwt.JwtTimeComponent;
import com.metlab_project.backend.security.jwt.JwtTokenProvider;
import com.metlab_project.backend.service.user.UserService;
import com.metlab_project.backend.domain.entity.jwt.RefreshEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        setFilterProcessesUrl("/api/users/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //클라이언트 요청에서 schoolEmail, password 추출
        String schoolEmail = request.getParameter("schoolEmail");
        String password = request.getParameter("password");

        log.info("[attemptAuthentication] schoolEmail = {}", schoolEmail);
        log.info("[attemptAuthentication] password = {}", password);

        //스프링 시큐리티에서 schoolEmail password를 검증하기 위해서는 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(schoolEmail, password, null);

        //token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken);
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
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
        //Refresh 토큰 저장
        addRefreshEntity(schoolEmail, refresh, jwtTimeComponent.getRefreshExpiration());

        /**
         * HTTP 인증 방식은 RFC 7235 정의에 따라 아래 인증 헤더 형태를 가져야 한다.
         * Authorization: 타입 인증토큰
         * e.g. Authorization: Bearer 인증토큰str
         */
        response.addHeader("Authorization", "Bearer " + access);
        response.addCookie(createCookie("refresh", refresh, jwtTimeComponent.getRefreshExpiration()));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void addRefreshEntity(String schoolEmail, String refresh, Long refreshExpireTime) {
        // 기존에 존재하는 Refresh 토큰 삭제
        refreshTokenRepository.findByUser_SchoolEmail(schoolEmail).ifPresent(refreshTokenRepository::delete);
    
        // 사용자 엔티티 조회
        User user = userRepository.findBySchoolEmail(schoolEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
    
        // 새로운 RefreshEntity 생성
        RefreshEntity refreshEntity = RefreshEntity.builder()
                .token(refresh)
                .expiration(refreshExpireTime)
                .user(user) // User 객체 연결
                .build();
    
        // 리프레시 토큰 저장
        refreshTokenRepository.save(refreshEntity);
    }
    

    private Cookie createCookie(String key, String value, Long refreshExpireTime) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(Math.toIntExact(refreshExpireTime / 1000));
        //cookie.setPath("/");
        return cookie;
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        log.info("[successfulAuthentication] 로그인 실패");
        response.setStatus(401);
    }
}