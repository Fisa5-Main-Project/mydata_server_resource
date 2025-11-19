package com.knowwhohow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SpringSecurity {

    private final Converter<Jwt, AbstractAuthenticationToken> ciBasedAuthenticationConverter;

    public SpringSecurity(Converter<Jwt, AbstractAuthenticationToken> ciBasedAuthenticationConverter) {
        this.ciBasedAuthenticationConverter = ciBasedAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http

                .oauth2ResourceServer(oauth2 -> oauth2
                                .jwt(jwt -> jwt
                                        // JWT 검증 성공 후, CI -> user_id 매핑 로직을 Converter에 위임
                                        .jwtAuthenticationConverter(ciBasedAuthenticationConverter)
                                )
                )

                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )


                // API 서버는 세션(쿠키)을 사용하지 않음 (Stateless)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // CSRF 비활성화 (Stateless API는 CSRF가 필요 없음)
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

}
