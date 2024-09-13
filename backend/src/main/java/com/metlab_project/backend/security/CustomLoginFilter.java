package com.metlab_project.backend.security;

import com.metlab_project.backend.domain.dto.user.res.UserInfoResponse;
import com.metlab_project.backend.repository.jwt.RefreshTokenRepository;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.Iterator;

@Slf4j
@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {
    /**
     * doFilterInternal을 오버라이딩하지 않아도 동작하는 이유.
     * Line46를 참고하면, 이 필터가 동작할 대상 URL을 지정해주는 것을 알 수 있음.
     * 필터를 더 효율적으로 동작시킬 수 있음.
     */

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

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

        final String schoolEmail = obtainUsername(request);
        final String password = obtainPassword(request);

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

        UserInfoResponse userInfo = userService.getUserInfoBySchoolEmail(schoolEmail);

        final String accessToken = jwtTokenProvider.generateAccessToken(userInfo.getSchoolEmail());
        final String refreshToken = jwtTokenProvider.generateRefreshToken(schoolEmail);

        log.info("[successfulAuthentication] schoolEmail = {}", schoolEmail);
        log.info("[successfulAuthentication] access token = {}", accessToken);
        log.info("[successfulAuthentication] refresh token = {}", refreshToken);

        addRefreshEntity(schoolEmail, refreshToken, jwtTokenProvider.getExpirationDateFromToken(refreshToken).getTime());

        /**
         * JWT 관리 정책
         * 1. accessToken -> Authorization Header에 포함
         * 2. refreshToken -> Cookie에 포함
         */
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(createCookie("refresh", refreshToken, jwtTokenProvider.getExpirationDateFromToken(refreshToken).getTime()));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void addRefreshEntity(String schoolEmail, String refresh, Long refreshExpireTime) {

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
        //cookie.setSecure(true);
        cookie.setPath("/");
        return cookie;
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        log.info("[successfulAuthentication] 로그인 실패");
        response.setStatus(401);
    }

}