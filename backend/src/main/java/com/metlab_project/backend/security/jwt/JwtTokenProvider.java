package com.metlab_project.backend.security.jwt;

import com.metlab_project.backend.domain.dto.user.UserInfoResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private static final long ACCESS_TOKEN_VALIDITY_TIME = 60 * 60 * 1000; // 엑세스 토큰의 유효 기간 = 1H
    private static final long REFRESH_TOKEN_VALIDITY_TIME = 7 * 24 * 60 * 60 * 1000; // 리프레쉬 토큰의 유효 기간 = 1W

    private final Key accessKey; //JWT(AccessToken) - Signature
    private final Key refreshKey; //JWT(RefreshToken) - Signature

    public JwtTokenProvider(@Value("${jwt.secret.access}") String accessKey, @Value("${jwt.secret.refresh}") String refreshKey) {
        this.accessKey = Keys.hmacShaKeyFor(accessKey.getBytes());
        this.refreshKey = Keys.hmacShaKeyFor(refreshKey.getBytes());
    }

    public String generateAccessToken(String schoolEmail, String nickname, String gender, String studentId, String college, String department){
        // 1. Header
        Map<String, Object> header = createJwtHeader();

        // 2. PayLoad
        Date now = new Date();
        Claims claims = Jwts.claims()
                .setIssuer("INU_INPPY")
                .setIssuedAt(now)
                .setSubject(schoolEmail)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALIDITY_TIME));

        claims.put("studentId", studentId);
        claims.put("nickname", nickname);
        claims.put("gender", gender);
        claims.put("college", college);
        claims.put("department", department);

        String token = Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .signWith(accessKey)
                .compact();

        return token;
    }

    public String generateRefreshToken(String schoolEmail){
        Date now = new Date();
        return Jwts.builder()
                .setIssuer("INU_INPPY")
                .setIssuedAt(now)
                .setSubject(schoolEmail)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALIDITY_TIME))
                .signWith(refreshKey)
                .compact();
    }

    public UserInfoResponse getUserInfoFromJwt(String accessToken){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

                return UserInfoResponse.builder()
                .schoolEmail(claims.getSubject())
                .nickname(claims.get("nickname", String.class))
                .gender(claims.get("gender", String.class))
                .studentId(claims.get("studentId", String.class))
                .college(claims.get("college", String.class))
                .department(claims.get("department", String.class))
                .build();
    }

    public String getSchoolEmailFromExpiredToken(String token){
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

    public Date getExpirationDateFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody().getExpiration();
    }

    public boolean isExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String getCategory(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(accessKey) // or refreshKey depending on the token type
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("category", String.class);
    }

    private Map<String, Object> createJwtHeader(){
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        return header;
    }
}