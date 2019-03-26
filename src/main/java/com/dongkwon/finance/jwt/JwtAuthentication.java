package com.dongkwon.finance.jwt;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthentication implements Authentication {
    private static final long serialVersionUID = 3829117118089686624L;
    private boolean authenticated;
    private final JwtClaim jwtClaim;

    public JwtAuthentication(JwtClaim jwtClaim) {
        this.jwtClaim = jwtClaim;
        authenticated = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getDetails() {
        return "";
    }

    @Override
    public JwtClaim getPrincipal() {
        return jwtClaim;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return "";
    }
}
