package com.metlab_project.backend.security.jwt;

import com.metlab_project.backend.exception.TokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenValidator {

    @Value("${jwt.secret.access}")
    private String accessTokenSecret;

    @Value("${jwt.secret.refresh}")
    private String refreshTokenSecret;

    public boolean validateAccessToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(accessTokenSecret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return true;
        } catch (ExpiredJwtException e) {
            throw new TokenException(TokenException.TokenErrorCode.TOKEN_EXPIRED);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException e) {
            throw new TokenException(TokenException.TokenErrorCode.TOKEN_INVALID);
        } catch (IllegalArgumentException e) {
            throw new TokenException(TokenException.TokenErrorCode.TOKEN_MISSING);
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
            throw new TokenException(TokenException.TokenErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException e) {
            throw new TokenException(TokenException.TokenErrorCode.TOKEN_INVALID);
        } catch (IllegalArgumentException e) {
            throw new TokenException(TokenException.TokenErrorCode.TOKEN_MISSING);
        }
    }
}
