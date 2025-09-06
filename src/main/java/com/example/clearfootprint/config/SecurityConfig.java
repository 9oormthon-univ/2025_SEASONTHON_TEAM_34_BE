package com.example.clearfootprint.config;

import com.example.clearfootprint.application.service.UserService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            if (authentication instanceof OAuth2AuthenticationToken) {
                                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                                OAuth2User oAuth2User = oauthToken.getPrincipal();

                                String email = oAuth2User.getAttribute("email");
                                String name = oAuth2User.getAttribute("name");
                                String picture = oAuth2User.getAttribute("picture");

                                userService.saveOrUpdateUser(email, name, picture);

                                response.sendRedirect("/fit-tester.html"); // 로그인 후 테스트 페이지로 이동
                            } else {
                                response.sendRedirect("/");
                            }
                        })
                );
        return http.build();
    }
}