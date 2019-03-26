package com.dongkwon.finance.jwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.dongkwon.finance.exception.InvalidTokenException;
import com.dongkwon.finance.exception.TokenNotFoundException;

public class JwtInterceptor extends HandlerInterceptorAdapter {
    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final TokenProvider tokenProvider;

    public JwtInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final String jwt = resolveToken(request);

        if (!StringUtils.hasText(jwt)) {
            throw new TokenNotFoundException("헤더에 jwt 토큰이 없습니다.");
        }

        try {
            final JwtClaim jwtClaim = tokenProvider.verifyToken(jwt);
            if (jwtClaim.getId() == null) {
                throw new InvalidTokenException("토큰에 id 정보가 없습니다.");
            }

            final JwtAuthentication jwtAuthentication = new JwtAuthentication(jwtClaim);

            SecurityContextHolder.getContext().setAuthentication(jwtAuthentication);
        } catch (JWTVerificationException exception){
            throw new InvalidTokenException("유효하지 않은 jwt 토큰입니다.");
        }

        return true;
    }

    private static String resolveToken(HttpServletRequest request){
        final String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(6).trim();
        }
        return null;
    }
}
