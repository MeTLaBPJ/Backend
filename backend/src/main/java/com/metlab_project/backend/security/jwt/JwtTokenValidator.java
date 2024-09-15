package com.metlab_project.backend.security.jwt;

import com.metlab_project.backend.exception.CustomErrorCode;
import com.metlab_project.backend.exception.CustomException;
import com.metlab_project.backend.service.jwt.BlacklistTokenService;
import com.metlab_project.backend.service.user.UserService;
import com.metlab_project.backend.domain.dto.user.res.UserInfoResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtTokenValidator {

    @Value("${jwt.secret.access}")
    private String accessTokenSecret;

    @Value("${jwt.secret.refresh}")
    private String refreshTokenSecret;

    private final BlacklistTokenService blacklistTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    public boolean validateAccessToken(String token) {
        if (blacklistTokenService.isBlacklisted(token))
            throw new CustomException(CustomErrorCode.TOKEN_BLACKLISTED);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(accessTokenSecret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomException(CustomErrorCode.TOKEN_EXPIRED);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException e) {
            throw new CustomException(CustomErrorCode.TOKEN_INVALID);
        } catch (IllegalArgumentException e) {
            throw new CustomException(CustomErrorCode.TOKEN_MISSING);
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(refreshTokenSecret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomException(CustomErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException e) {
            throw new CustomException(CustomErrorCode.TOKEN_INVALID);
        } catch (IllegalArgumentException e) {
            throw new CustomException(CustomErrorCode.TOKEN_MISSING);
        }
    }

    public void handleTokenException(HttpServletRequest request, HttpServletResponse response, CustomException e,
                                     String accessToken, UserService userService, JwtTokenProvider jwtTokenProvider) throws IOException {
        switch (e.getCustomErrorCode()) {
            case TOKEN_EXPIRED:
                handleExpiredToken(request, response, accessToken, userService, jwtTokenProvider);
                break;
            case TOKEN_INVALID:
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                break;
            case TOKEN_MISSING:
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Token is missing");
                break;
            case TOKEN_BLACKLISTED:
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Token is blacklisted");
                break;
            default:
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }

    private void handleExpiredToken(HttpServletRequest request, HttpServletResponse response, String accessToken, UserService userService, JwtTokenProvider jwtTokenProvider) throws IOException {
        try {
            String schoolEmail = jwtTokenProvider.getSchoolEmailFromExpiredToken(accessToken);
            String refreshToken = getRefreshTokenFromCookie(request);

            if ((refreshToken != null) && validateRefreshToken(refreshToken)) {
                UserInfoResponse user = userService.getUserInfoBySchoolEmail(schoolEmail);
                String newAccessToken = jwtTokenProvider.generateAccessToken(user.getSchoolEmail());

                // Set the new access token in the Authorization header
                response.setHeader("Authorization", "Bearer " + newAccessToken);

                // Generate a new refresh token and set it as an HTTP-only cookie
                String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getSchoolEmail());
                ResponseCookie refreshTokenCookie = jwtTokenProvider.generateRefreshTokenCookie(newRefreshToken);
                response.setHeader("Set-Cookie", refreshTokenCookie.toString());

                Authentication auth = new UsernamePasswordAuthenticationToken(user.getSchoolEmail(), null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (CustomException error) {
            SecurityContextHolder.clearContext();

            // Clear the refresh token cookie
            ResponseCookie clearRefreshTokenCookie = ResponseCookie.from("refresh", "")
                    .maxAge(0)
                    .path("/")
                    .build();
            response.setHeader("Set-Cookie", clearRefreshTokenCookie.toString());

            response.sendRedirect("/login");
            response.getWriter().write("Token error: " + error.getMessage());
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("REFRESH_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}