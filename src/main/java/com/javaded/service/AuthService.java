package com.javaded.service;

import com.javaded.web.dto.auth.JwtRequest;
import com.javaded.web.dto.auth.JwtResponse;

public interface AuthService {

    JwtResponse login(
            JwtRequest loginRequest
    );

    JwtResponse refresh(
            String refreshToken
    );
}
