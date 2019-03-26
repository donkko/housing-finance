package com.dongkwon.finance.controller;

import javax.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dongkwon.finance.controller.request.JwtRequest;
import com.dongkwon.finance.controller.response.JwtResponse;
import com.dongkwon.finance.jwt.JwtClaim;
import com.dongkwon.finance.jwt.TokenProvider;
import com.dongkwon.finance.service.AccountService;

@RestController
@RequestMapping("/api/jwt")
public class JwtController {
    private final AccountService accountService;
    private final TokenProvider tokenProvider;

    public JwtController(AccountService accountService, TokenProvider tokenProvider) {
        this.accountService = accountService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/signup")
    public JwtResponse signUp(@Valid @RequestBody JwtRequest jwtRequest) {
        accountService.registerAccount(jwtRequest.getId(), jwtRequest.getPw());

        return JwtResponse.of(tokenProvider.generateToken(jwtRequest.getId()));
    }

    @PostMapping("/signin")
    public JwtResponse signIn(@Valid @RequestBody JwtRequest jwtRequest) {
        accountService.validateAccount(jwtRequest.getId(), jwtRequest.getPw());

        return JwtResponse.of(tokenProvider.generateToken(jwtRequest.getId()));
    }

    @PostMapping("/refresh")
    public JwtResponse refresh() {
        final JwtClaim jwtClaim =
                (JwtClaim) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return JwtResponse.of(tokenProvider.generateToken(jwtClaim.getId()));
    }
}
