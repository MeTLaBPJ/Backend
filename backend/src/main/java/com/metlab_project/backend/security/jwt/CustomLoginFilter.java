package com.metlab_project.backend.security.jwt;

import com.metlab_project.backend.repository.user.RefreshTokenRepository;
import com.metlab_project.backend.service.user.UserService;

import io.jsonwebtoken.io.IOException;

import com.metlab_project.backend.domain.entity.RefreshEntity;
import com.metlab_project.backend.domain.dto.user.UserInfoResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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
    private final JwtTokenProvider jwtTokenProvider; // JwtTokenProvider 사용
    private final UserService userService; // UserService 사용

    public CustomLoginFilter(AuthenticationManager authenticationManager, 
                             RefreshTokenRepository refreshTokenRepository, 
                             JwtTokenProvider jwtTokenProvider,
                             UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        setFilterProcessesUrl("/api/users/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // 클라이언트 요청에서 username(schoolEmail), password 추출
        final String schoolEmail = obtainUsername(request); // schoolEmail로 변경
        final String password = obtainPassword(request);

        log.info("[attemptAuthentication] schoolEmail = {}", schoolEmail);
        log.info("[attemptAuthentication] password = {}", password);

        // 스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(schoolEmail, password, null);

        // token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken);
    }

    // 로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        log.info("[successfulAuthentication] 로그인 성공");

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        final String schoolEmail = authentication.getName();
        final String role = auth.getAuthority();

        // UserService를 통해 유저 정보 가져오기
        UserInfoResponse userInfo = userService.getUserInfoBySchoolEmail(schoolEmail);

        // JwtTokenProvider를 사용하여 Access/Refresh 토큰 발급
        final String accessToken = jwtTokenProvider.generateAccessToken(
                userInfo.getSchoolEmail(),
                userInfo.getNickname(),
                userInfo.getGender(),
                userInfo.getStudentId(),
                userInfo.getCollege(),
                userInfo.getDepartment()
        );
        final String refreshToken = jwtTokenProvider.generateRefreshToken(schoolEmail);

        log.info("[successfulAuthentication] schoolEmail = {}", schoolEmail);
        log.info("[successfulAuthentication] access token = {}", accessToken);
        log.info("[successfulAuthentication] refresh token = {}", refreshToken);

        // Refresh 토큰 저장
        addRefreshEntity(schoolEmail, refreshToken, jwtTokenProvider.getExpirationDateFromToken(refreshToken).getTime());

        // Authorization 헤더와 쿠키 설정
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(createCookie("refresh", refreshToken, jwtTokenProvider.getExpirationDateFromToken(refreshToken).getTime()));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void addRefreshEntity(String schoolEmail, String refresh, Long refreshExpireTime) {
        // 기존에 존재하는 Refresh 토큰 삭제
        refreshTokenRepository.findBySchoolEmail(schoolEmail).ifPresent(refreshTokenRepository::delete);

        RefreshEntity refreshEntity = RefreshEntity.builder()
                .schoolEmail(schoolEmail)
                .token(refresh)
                .expiration(refreshExpireTime)
                .build();

        refreshTokenRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value, Long refreshExpireTime) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(Math.toIntExact(refreshExpireTime / 1000));
        cookie.setSecure(true); // HTTPS에서만 전송
        cookie.setPath("/"); // 경로 지정
        return cookie;
    }

   //로그인 실패시 실행하는 메소드
   @Override
   protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
       log.info("[successfulAuthentication] 로그인 실패");
       response.setStatus(401);
   }

}
