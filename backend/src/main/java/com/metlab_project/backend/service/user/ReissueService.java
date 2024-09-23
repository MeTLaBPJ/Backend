package com.metlab_project.backend.service.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.metlab_project.backend.domain.entity.jwt.RefreshEntity;
import com.metlab_project.backend.domain.entity.user.User;
import com.metlab_project.backend.repository.jwt.RefreshTokenRepository;
import com.metlab_project.backend.repository.user.UserRepository;
import com.metlab_project.backend.security.jwt.JwtTokenProvider;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReissueService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public ResponseEntity<?> reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 리프레시 토큰 가져오기
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return new ResponseEntity<>("쿠키가 없습니다", HttpStatus.BAD_REQUEST);
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        // 리프레시 토큰 검증
        ResponseEntity<String> validationResponse = verifiedRefreshToken(refresh);
        if (validationResponse != null) return validationResponse;

        // 토큰에서 사용자 이름 추출
        String username = jwtTokenProvider.getSchoolEmailFromExpiredToken(refresh);

        // 사용자 엔티티 조회
        User user = userRepository.findBySchoolEmail(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 새로운 JWT 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(username);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);

        // 기존 리프레시 토큰 삭제
        refreshTokenRepository.findByToken(refresh).ifPresent(refreshTokenRepository::delete);

        // 새로운 리프레시 토큰 데이터베이스에 저장
        RefreshEntity refreshEntity = RefreshEntity.builder()
                .token(newRefreshToken)
                .expiration(jwtTokenProvider.getRefreshTokenValidityTime() / 1000) // 초 단위로 변환
                .user(user)
                .build();
        refreshTokenRepository.save(refreshEntity);

        // 응답에 새로운 JWT 추가
        response.addHeader("Authorization", "Bearer " + newAccessToken);
        response.addCookie(jwtTokenProvider.generateRefreshTokenCookie(newRefreshToken)); // 수정된 부분

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseEntity<String> verifiedRefreshToken(String refresh) {
        if (refresh == null) {
            return new ResponseEntity<>("리프레시 토큰이 없습니다", HttpStatus.BAD_REQUEST);
        }

        // 토큰 만료 여부 확인
        try {
            jwtTokenProvider.getExpirationDateFromToken(refresh); // 만료된 토큰이면 ExpiredJwtException 발생
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("리프레시 토큰이 만료되었습니다", HttpStatus.BAD_REQUEST);
        }

        // 데이터베이스에 토큰이 존재하는지 확인
        if (!refreshTokenRepository.existsByToken(refresh)) {
            return new ResponseEntity<>("유효하지 않은 리프레시 토큰입니다", HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}
