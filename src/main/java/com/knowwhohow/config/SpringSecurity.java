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
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    // custom filter 주입
    public SpringSecurity(JwtAuthorizationFilter jwtAuthorizationFilter, RestAuthenticationEntryPoint restAuthenticationEntryPoint) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // Custom Filter 등록
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
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
