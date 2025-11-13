package com.knowwhohow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class RsaKeyConfig {

    @Bean
    public PublicKey publicKeyFromAS() {
        try {
            // AS의 KeyPairGenerator 로직과 동일하게 RSA 키 페어를 생성합니다.
            KeyPair keyPair = generateRsaKey();
            return (RSAPublicKey) keyPair.getPublic();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to create RSA Public Key Bean for JWT validation.", ex);
        }
    }

    // AS의 KeyPairGenerator 로직 복사
    private static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate RSA KeyPair.", ex);
        }
    }
}