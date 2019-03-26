package com.dongkwon.finance.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dongkwon.finance.jwt.JwtInterceptor;
import com.dongkwon.finance.jwt.JwtSettings;
import com.dongkwon.finance.jwt.TokenProvider;

@Configuration
@EnableConfigurationProperties(JwtSettings.class)
public class AuthConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenProvider tokenProvider(JwtSettings jwtSettings) {
        return new TokenProvider(jwtSettings);
    }

    @Bean
    public JwtInterceptor jwtInterceptor(TokenProvider tokenProvider) {
        return new JwtInterceptor(tokenProvider);
    }
}
