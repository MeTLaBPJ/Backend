package com.metlab_project.backend.security;

import com.metlab_project.backend.security.jwt.JwtAuthenticationFilter;
import com.metlab_project.backend.security.jwt.JwtTokenProvider;
import com.metlab_project.backend.security.jwt.JwtTokenValidator;
import com.metlab_project.backend.service.jwt.BlacklistTokenService;
import com.metlab_project.backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenValidator jwtTokenValidator;
    private final BlacklistTokenService blacklistTokenService;

    private static final List<String> whiteListUrl = Arrays.asList(
            "/api/auth/login",
            "/sign-up/email",
            "/api/auth/register",
            "/api/**",
            "/error/**",
            "/api/users/join",
            "/api/users/login"
    );

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Value("${cors.allowed-methods}")
    private List<String> allowedMethods;

    @Value("${cors.allowed-headers}")
    private List<String> allowedHeaders;

    @Value("${cors.exposed-headers}")
    private List<String> exposedHeaders;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(whiteListUrl.toArray(new String[0])).permitAll()
                    //.anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(userService, jwtTokenProvider,jwtTokenValidator,blacklistTokenService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setExposedHeaders(exposedHeaders);
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
