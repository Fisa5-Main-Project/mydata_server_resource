package com.knowwhohow.filter;

import com.knowwhohow.entity.BankUser;
import com.knowwhohow.repository.BankUserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.security.Key;
import java.security.PublicKey;
import java.util.Collections;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final BankUserRepository bankUserRepository;
    // signingKey = AS의 공개 키
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
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(this.signingKey) // Key를 설정
                    .build()
                    .parseClaimsJws(token) // 서명 및 만료일 검증
                    .getBody();

            // 3. Payload에서 CI 추출 (CI == user_code)
            String ci = claims.get(CI_CLAIM_NAME, String.class);

            // 4. CI -> user_id 매핑 및 Context 설정
            if (ci != null) {
                setupAuthentication(ci);
            }

        } catch (UsernameNotFoundException e) {
            // DB에 사용자가 없는 경우, 401 Unauthorized로 처리하여 접근 차단
            SecurityContextHolder.clearContext();

            throw new AuthenticationCredentialsNotFoundException("User not found in DB: Invalid CI mapping.");

        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();

            throw new AuthenticationCredentialsNotFoundException("JWT validation failed: " + e.getMessage());

        }

        filterChain.doFilter(request, response);
    }

    private void setupAuthentication(String ci) {
        BankUser bankUser = bankUserRepository.findByUserCode(ci)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with CI: " + ci));

        Long userId = bankUser.getUserId();

        // 조회된 user_id를 Principal 객체로 설정 -> Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                Collections.emptyList()
        );

        // 생성된 Authentication 객체를 Security Context에 등록
        // 등록된 user_id가 필터 체인 이후 인가의 최종 기준이 됨
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }
}