package com.knowwhohow.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowwhohow.global.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 이 Bean이 SpringSecurity.java에 주입되어 사용됨

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        // 1. 상태 코드 및 컨텐츠 타입 설정
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 2. ApiResponse 구조를 사용하여 401 응답 생성
        String errorMessage = "인증 실패: 유효하지 않거나 만료된 토큰입니다.";

        // 필터에서 던져진 구체적인 오류 메시지를 사용
        if (authException.getMessage() != null) {
            errorMessage = authException.getMessage();
        }

        String jsonResponse = objectMapper.writeValueAsString(
                ApiResponse.onFailure("AUTH_FAILED", errorMessage)
        );

        // 3. 응답 전송
        response.getWriter().write(jsonResponse);
    }
}