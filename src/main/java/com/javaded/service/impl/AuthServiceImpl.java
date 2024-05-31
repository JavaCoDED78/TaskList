package com.javaded.service.impl;

import com.javaded.domain.user.User;
import com.javaded.service.AuthService;
import com.javaded.service.UserService;
import com.javaded.web.dto.auth.JwtRequest;
import com.javaded.web.dto.auth.JwtResponse;
import com.javaded.web.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public JwtResponse login(
            final JwtRequest loginRequest
    ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword())
        );

        User user = userService.getByUsername(loginRequest.getUsername());
        return new JwtResponse(
                user.getId(),
                user.getUsername(),
                jwtTokenProvider.createAccessToken(
                        user.getId(),
                        user.getUsername(),
                        user.getRoles()
                ),
                jwtTokenProvider.createRefreshToken(
                        user.getId(),
                        user.getUsername()
                )
        );
    }

    @Override
    public JwtResponse refresh(
            final String refreshToken
    ) {
        return jwtTokenProvider.refreshUserTokens(refreshToken);
    }
}
