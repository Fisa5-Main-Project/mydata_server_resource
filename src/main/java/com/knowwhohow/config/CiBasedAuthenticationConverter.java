package com.knowwhohow.config;

import com.knowwhohow.entity.BankUser;
import com.knowwhohow.repository.BankUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class CiBasedAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final BankUserRepository bankUserRepository;
    private final HashUtil hashUtil;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // 1. JWT의 Payload에서 CI 클레임 추출
        String encryptedCi = jwt.getSubject();

        String originCi;
        try {
            originCi = AesUtil.decrypt(encryptedCi);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid Token: Cannot decrypt CI.", e);
        }

        // 2. CI를 사용하여 DB에서 BankUser(user_id) 조회
        Long userId = bankUserRepository.findByUserCodeHash(hashUtil.generateHash(originCi))
                .map(BankUser::getUserId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for the given token."));

        // 3. user_id를 Principal로 하는 Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(
                userId,          // Principal: user_id (Long)
                null,            // Credentials: 토큰 검증이 끝났으므로 null
                Collections.emptyList() // Authorities: 권한은 현재 사용하지 않음
        );
    }
}