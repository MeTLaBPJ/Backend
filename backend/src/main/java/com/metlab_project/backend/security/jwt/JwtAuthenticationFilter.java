package com.metlab_project.backend.security.jwt;

import com.metlab_project.backend.domain.dto.user.CustomUserDetails;
import com.metlab_project.backend.domain.dto.user.UserInfoResponse;
import com.metlab_project.backend.domain.dto.user.UserRole;
import com.metlab_project.backend.domain.entity.User;
import com.metlab_project.backend.exception.TokenException;
import com.metlab_project.backend.service.jwt.BlacklistTokenService;
import com.metlab_project.backend.service.user.UserService;
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
import java.util.Collections;
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
            "/api/auth/register"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        logger.info("Enter JwtTokenFilter with {}", request.getRequestURI());
        String accessToken = getTokenFromHttpRequest(request);

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

            Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails,null, customUserDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request,response);

        } catch (TokenException e) {
            logger.warn("JwtException Occurred");
            handleTokenException(request, response, e, accessToken);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("UnExpected Error with : " + e.getMessage());
        }
    }

    private String getTokenFromHttpRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        logger.error("Get Token From Http Request Failed!");
        return null;
    }

    private boolean isPermittedUrl(String requestUri) {
        return whiteListUrl.stream()
                .anyMatch(uri -> pathMatcher.match(uri, requestUri));
    }

    private void handleTokenException(HttpServletRequest request, HttpServletResponse response, TokenException e, String accessToken) throws IOException {
        switch (e.getErrorCode()) {
            case TOKEN_EXPIRED:
                try {
                    String schoolEmail = jwtTokenProvider.getSchoolEmailFromExpiredToken(accessToken);
                    String refreshToken = userService.getRefreshToken(schoolEmail);

                    if ((refreshToken != null) && jwtTokenValidator.validateRefreshToken(refreshToken)) {
                        UserInfoResponse user = userService.getUserInfoBySchoolEmail(schoolEmail);
                        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getSchoolEmail(), user.getNickname(), user.getGender(),
                                user.getStudentId(), user.getCollege(), user.getDepartment());

                        Cookie cookie = new Cookie("JWT", newAccessToken);
                        cookie.setHttpOnly(true);
                        cookie.setPath("/");
                        cookie.setMaxAge(60 * 60);

                        response.addCookie(cookie);

                        Authentication auth = new UsernamePasswordAuthenticationToken(user.getSchoolEmail(), null, Collections.emptyList());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } catch (TokenException error) {
                    SecurityContextHolder.clearContext();

                    Cookie jwtCookie = new Cookie("JWT", null);
                    jwtCookie.setMaxAge(0);
                    jwtCookie.setPath("/");

                    response.addCookie(jwtCookie);
                    response.sendRedirect("/login");
                    response.getWriter().write("Token error: " + error.getMessage());
                }
                break;
            case TOKEN_INVALID:
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                break;
            case TOKEN_MISSING:
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Token is missing");
                break;
            case TOKEN_BLACKLISTED:
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Token is blackListed");
            default:
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
