package com.metlab_project.backend.security;

import java.io.IOException;

import org.springframework.web.filter.GenericFilterBean;

import com.metlab_project.backend.repository.jwt.RefreshTokenRepository;
import com.metlab_project.backend.security.jwt.JwtTokenProvider;
import com.metlab_project.backend.security.jwt.JwtTokenValidator;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenValidator jwtTokenValidator;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    /**
     * 로그아웃 필터
     * @param request
     * @param response
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        // Logout Request 검증
        if (verifiedLogoutRequest(request, response, filterChain)) return;

        // Cookie에서 Refresh Token 가져오기
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            doStatusBadRequest("[doFilter] cookies are null", response);
            return;
        }
        for (Cookie cookie : cookies) {
            if ("refresh".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
            }
        }

        // Refresh Token 검증
        if (!jwtTokenValidator.validateRefreshToken(refreshToken)) return;

        // 로그아웃 진행
        log.info("[doFilter] Logging out");

        // Refresh 토큰 DB에서 제거
        refreshTokenRepository.findByToken(refreshToken).ifPresent(refreshTokenRepository::delete);

        // Refresh 토큰 Cookie 삭제
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * 로그아웃 요청 검증
     * @param request
     * @param response
     * @param filterChain
     * @return boolean
     * @throws IOException
     * @throws ServletException
     */
    private static boolean verifiedLogoutRequest(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        // 로그아웃 요청이 /api/users/logout으로 와야 하고, 메서드는 POST여야 함
        final String requestUri = request.getRequestURI();
        if (!"/api/auth/logout".equals(requestUri)) {
            filterChain.doFilter(request, response);
            return true;
        }
        final String requestMethod = request.getMethod();
        if (!"POST".equals(requestMethod)) {
            filterChain.doFilter(request, response);
            return true;
        }
        return false;
    }

    private static void doStatusBadRequest(String msg, HttpServletResponse response) {
        log.info(msg);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
