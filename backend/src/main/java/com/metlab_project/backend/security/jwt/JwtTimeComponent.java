package com.metlab_project.backend.security.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * LoginFilter가 Component가 아니기 때문에 @Autowired로 주입이 불가능하기
 * 때문에 @Value를 사용하기 위해 Component로 설정
 */
@Getter
@Component
public class JwtTimeComponent {

    @Value("${spring.jwt.expiration.access}")
    private Long accessExpiration;

    @Value("${spring.jwt.expiration.refresh}")
    private Long refreshExpiration;
}
