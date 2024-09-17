package com.metlab_project.backend.service.jwt;

import com.metlab_project.backend.domain.entity.jwt.BlacklistToken;
import com.metlab_project.backend.repository.jwt.BlacklistTokenRepository;
import com.metlab_project.backend.security.jwt.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;

@Service
@AllArgsConstructor
public class BlacklistTokenService {

    private final BlacklistTokenRepository blacklistTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public void addBlacklistToken(String token){
        if (token == null) {
            throw new IllegalArgumentException("Token must not be null");
        }

        Date expiryDate = jwtTokenProvider.getExpirationDateFromToken(token);
        BlacklistToken blacklistToken = new BlacklistToken();

        blacklistToken.setToken(token);
        blacklistToken.setExpiryDate(expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        blacklistTokenRepository.save(blacklistToken);
    }

    public boolean isBlacklisted(String token){
        if (token == null) {
            throw new IllegalArgumentException("Token must not be null");
        }

        return blacklistTokenRepository.existsByToken(token);
    }
}
