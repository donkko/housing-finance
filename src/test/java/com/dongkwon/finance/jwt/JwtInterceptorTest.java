package com.dongkwon.finance.jwt;

import static com.dongkwon.finance.jwt.JwtInterceptor.AUTHORIZATION_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.dongkwon.finance.exception.InvalidTokenException;
import com.dongkwon.finance.exception.TokenNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class JwtInterceptorTest {
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private TokenProvider tokenProvider;
    @InjectMocks
    private JwtInterceptor jwtInterceptor;

    @Test
    public void testPreHandlePassed() {
        // given
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn("Bearer ABCD");
        when(tokenProvider.verifyToken(any())).thenReturn(JwtClaim.of("someId", Instant.now()));

        // when
        boolean result = jwtInterceptor.preHandle(httpServletRequest, null, null);

        // then
        assertThat(result).isTrue();
    }

    @Test(expected = TokenNotFoundException.class)
    public void testPreHandleNoToken() {
        // given
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn("");

        // when
        boolean result = jwtInterceptor.preHandle(httpServletRequest, null, null);

        // then
        // Expect TokenNotFoundException to be thrown
    }

    @Test(expected = InvalidTokenException.class)
    public void testPreHandleExpiredToken() {
        // given
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn("Bearer ABCD");
        when(tokenProvider.verifyToken(any())).thenThrow(new TokenExpiredException(""));

        // when
        boolean result = jwtInterceptor.preHandle(httpServletRequest, null, null);

        // then
        // Expect InvalidTokenException to be thrown
    }

    @Test(expected = InvalidTokenException.class)
    public void testPreHandleTokenWithoutId() {
        // given
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn("Bearer ABCD");
        when(tokenProvider.verifyToken(any())).thenReturn(JwtClaim.of(null, Instant.now()));

        // when
        boolean result = jwtInterceptor.preHandle(httpServletRequest, null, null);

        // then
        // Expect InvalidTokenException to be thrown
    }
}
