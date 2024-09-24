package com.metlab_project.backend.security.jwt;

import com.metlab_project.backend.domain.dto.user.res.UserInfoResponse;
import com.metlab_project.backend.domain.entity.user.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.Cookie;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private static final long ACCESS_TOKEN_VALIDITY_TIME = 60 * 60 * 12000; // 엑세스 토큰의 유효 기간 = 12H
    private static final long REFRESH_TOKEN_VALIDITY_TIME = 7 * 24 * 60 * 60 * 1000; // 리프레쉬 토큰의 유효 기간 = 1W

    private final Key accessKey; // JWT(AccessToken) - Signature
    private final Key refreshKey; // JWT(RefreshToken) - Signature

    public JwtTokenProvider(@Value("${jwt.secret.access}") String accessKey, @Value("${jwt.secret.refresh}") String refreshKey) {
        this.accessKey = Keys.hmacShaKeyFor(accessKey.getBytes());
        this.refreshKey = Keys.hmacShaKeyFor(refreshKey.getBytes());
    }

    public UserInfoResponse getUserInfo(String accessToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

        String schoolEmail = claims.getSubject();
        UserRole role = UserRole.valueOf(claims.get("role", String.class));

        return new UserInfoResponse(schoolEmail, role);
    }

    public String getSchoolEmailFromExpiredToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    public Date getExpirationDateFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody().getExpiration();
    }

    public String generateAccessToken(String schoolEmail) {
        Map<String, Object> header = createJwtHeader();

        Date now = new Date();
        Claims claims = Jwts.claims()
                .setIssuer("INU_INPPY")
                .setIssuedAt(now)
                .setSubject(schoolEmail)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALIDITY_TIME));
        claims.put("role", UserRole.ROLE_USER.toString());

        return Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .signWith(accessKey)
                .compact();
    }

    public String generateRefreshToken(String schoolEmail) {
        Date now = new Date();
        return Jwts.builder()
                .setIssuer("INU_INPPY")
                .setIssuedAt(now)
                .setSubject(schoolEmail)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALIDITY_TIME))
                .signWith(refreshKey)
                .compact();
    }

    // ResponseCookie를 Cookie로 변환하여 반환
    public Cookie generateRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie("REFRESH_TOKEN", refreshToken);
        cookie.setHttpOnly(true);
        // cookie.setSecure(true); // HTTPS를 사용하는 경우에만 true로 설정
        cookie.setPath("/");
        cookie.setMaxAge((int) (REFRESH_TOKEN_VALIDITY_TIME / 1000)); // 쿠키 유효 기간 (초 단위)
        return cookie;
    }

    private Map<String, Object> createJwtHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        return header;
    }

    // 새로운 메서드 추가: REFRESH_TOKEN_VALIDITY_TIME 값을 반환
    public long getRefreshTokenValidityTime() {
        return REFRESH_TOKEN_VALIDITY_TIME;
    }
}
