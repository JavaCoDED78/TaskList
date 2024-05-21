package com.javaded.service.impl;

import com.javaded.service.AuthService;
import com.javaded.web.dto.auth.JwtRequest;
import com.javaded.web.dto.auth.JwtResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public JwtResponse login(JwtRequest loginRequest) {
        return null;
    }

    @Override
    public JwtResponse refresh(String refreshToken) {
        return null;
    }
}
