package com.metlab_project.backend.security.jwt;

import com.metlab_project.backend.exception.CustomException;
import com.metlab_project.backend.service.jwt.BlacklistTokenService;
import com.metlab_project.backend.service.user.UserService;
import com.metlab_project.backend.domain.entity.user.*;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter implements Filter {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenValidator jwtTokenValidator;
    private final BlacklistTokenService blacklistTokenService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final List<String> whiteListUrl = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/sign-up/email",
            "/api/users/join",
            "/api/users/login",
            "/sign-up/email/check",
            "/isExist/**"

    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        logger.info("Processing request in JwtAuthenticationFilter: {}", request.getRequestURI());
        String accessToken = getAccessTokenFromHeader(request);

        if (isPermittedUrl(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtTokenValidator.validateAccessToken(accessToken);

            String username = jwtTokenProvider.getUserInfo(accessToken).getSchoolEmail();
            UserRole userRole = jwtTokenProvider.getUserInfo(accessToken).getRole();

            User user = User.builder()
                    .schoolEmail(username)
                    .role(userRole)
                    .build();

            CustomUserDetails customUserDetails = new CustomUserDetails(user);

            Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (CustomException e) {
            logger.warn("CustomException occurred during token validation: {}", e.getMessage());
            jwtTokenValidator.handleTokenException(request, response, e, accessToken, userService);
        } catch (Exception e) {
            logger.error("Unexpected error in JwtAuthenticationFilter: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Unexpected error: " + e.getMessage());
        }
    }

    private String getAccessTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        logger.debug("Access token not found in request header");
        return null;
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        logger.debug("Refresh token not found in cookies");
        return null;
    }

    private boolean isPermittedUrl(String requestUri) {
        return whiteListUrl.stream()
                .anyMatch(uri -> pathMatcher.match(uri, requestUri));
    }
}