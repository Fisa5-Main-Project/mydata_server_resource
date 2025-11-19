package com.knowwhohow.config;

import com.knowwhohow.repository.BankUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class CiBasedAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final BankUserRepository bankUserRepository;
    private static final String CI_CLAIM_NAME = "ci";

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // 1. JWT의 Payload에서 CI 클레임 추출
        String ci = jwt.getClaimAsString(CI_CLAIM_NAME);

        if (ci == null) {
            // CI 클레임이 없으면 인증 실패로 간주
            throw new UsernameNotFoundException("CI claim is missing in the JWT.");
        }

        // 2. CI를 사용하여 DB에서 BankUser(user_id) 조회
        Long userId = bankUserRepository.findByUserCode(ci)
                .map(bankUser -> bankUser.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found for the given token."));

        // 3. user_id를 Principal로 하는 Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(
                userId,          // Principal: user_id (Long)
                null,            // Credentials: 토큰 검증이 끝났으므로 null
                Collections.emptyList() // Authorities: 권한은 현재 사용하지 않음
        );
    }
}