package com.example.clearfootprint.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF: SPA + 토큰 인증이면 비활성화가 일반적
                .csrf(AbstractHttpConfigurer::disable)

                // CORS 허용 (필요시 도메인/헤더 조정)
                .cors(Customizer.withDefaults())

                // 세션은 사용하지 않음(프론트 토큰 방식 전제)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 임시 전부 허용 (백엔드 JWT 검증 붙일 때 아래 anyRequest().authenticated()로 바꾸고 필터 추가)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index.html",
                                "/login/**", "/auth/**", "/public/**",
                                "/fit-tester.html",
                                "/css/**", "/js/**", "/images/**", "/favicon.ico",
                                "/actuator/health"
                        ).permitAll()
                        .anyRequest().permitAll()
                );

        // JWT 등의 토큰 검증을 붙일 땐 여기서 커스텀 필터 추가해주세요
        // ex)
        // .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS 기본 설정 (필요한 도메인/헤더/메소드로 커스터마이즈 가능)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // TODO: 운영에선 허용 도메인을 구체적으로 제한하기 (예: https://app.example.com)
        config.setAllowedOriginPatterns(List.of("*"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Location"));
        config.setAllowCredentials(true); // 필요 없으면 false로

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // JWT 검증을 구현할 때 사용할 필터 빈 예시
    // @Bean
    // public OncePerRequestFilter jwtAuthenticationFilter() {
    //     return new JwtAuthenticationFilter(tokenValidator);
    // }
}
