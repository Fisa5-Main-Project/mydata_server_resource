package com.knowwhohow.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class RsaKeyConfig {

    // 1. application.yml에서 AS의 공개 키 주입
    @Value("${mydata.auth.public-key}")
    private String publicKey;

    @Bean
    public PublicKey publicKeyFromAS() {
        try {
            // 2. 문자열 디코딩
            byte[] keyBytes = Base64.getDecoder().decode(publicKey);

            // 3. 인코딩 키 사양 생성
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

            // 4. PublicKey 객체 생성 (RSA)
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            // 5. 객체 반환
            return keyFactory.generatePublic(keySpec);

        } catch (Exception ex) {
            throw new IllegalStateException("Failed to load and create RSA Public Key from configuration. Check 'mydata.auth.public-key' value.", ex);
        }
    }
}