package com.knowwhohow.config;

import com.knowwhohow.filter.JwtAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SpringSecurity {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    // custom filter 주입
    public SpringSecurity(JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // Custom Filter 등록
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )

                // OAuth2 리소스 서버 기능 활성화
                // (yml의 issuer-uri를 바탕으로 JWT 서명 및 만료시간을 검증)
                //.oauth2ResourceServer(oauth2 -> oauth2
                //        .jwt(Customizer.withDefaults())
                //)
                // custom filter 사용으로 제거(제거 전 잠시 주석처리로 대체)

                // API 서버는 세션(쿠키)을 사용하지 않음 (Stateless)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // CSRF 비활성화 (Stateless API는 CSRF가 필요 없음)
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

}
