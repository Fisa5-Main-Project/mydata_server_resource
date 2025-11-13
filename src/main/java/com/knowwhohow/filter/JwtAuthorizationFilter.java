package com.knowwhohow.filter;

import com.knowwhohow.repository.BankUserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.security.Key;
import java.security.PublicKey;
import java.util.Collections;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final BankUserRepository bankUserRepository;
    private final Key signingKey;

    private static final String CI_CLAIM_NAME = "ci";

    public JwtAuthorizationFilter(BankUserRepository bankUserRepository, PublicKey publicKeyFromAS) {
        this.bankUserRepository = bankUserRepository;
        this.signingKey = publicKeyFromAS;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // 1. 필수 요소 확인: Bearer 토큰이 있어야 인가 시도
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // 2. Access Token 유효성 검증(RS256 서명 검증) 및 CI 추출
            Claims claims = Jwts.parser()
                    .setSigningKey(this.signingKey) // 👈 Key를 설정
                    .parseClaimsJws(token) // 👈 서명 및 만료일 검증
                    .getBody();

            // 3. Payload에서 CI 클레임 추출 (CI == user_code)
            String ci = claims.get(CI_CLAIM_NAME, String.class);

            // 4. CI -> user_id 매핑 및 Context 설정
            if (ci != null) {
                setupAuthentication(ci);
            }

        } catch (JwtException e) {
            // 서명 오류 (토큰 변조) 시 401 Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT Signature validation failed (Token Tampered).");
            return;
        } catch (Exception e) {
            // 토큰 유효성 검증 실패 시 401 Unauthorized 응답
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT token is invalid or expired.");
            return; // 필터 체인 중단
        }

        filterChain.doFilter(request, response);
    }

    private void setupAuthentication(String ci) {
        // CI를 사용하여 내부 DB에서 user_id를 조회하는 기존 로직은 유지됩니다.
        bankUserRepository.findByUserCode(ci).ifPresent(bankUser -> {
            Long userId = bankUser.getUserId();

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    Collections.emptyList()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        });
    }
}