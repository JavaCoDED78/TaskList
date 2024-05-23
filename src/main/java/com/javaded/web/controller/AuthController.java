package com.javaded.web.controller;

import com.javaded.domain.user.User;
import com.javaded.service.AuthService;
import com.javaded.service.UserService;
import com.javaded.web.dto.auth.JwtRequest;
import com.javaded.web.dto.auth.JwtResponse;
import com.javaded.web.dto.user.UserDto;
import com.javaded.web.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;


    @RequestMapping("/login")
    public JwtResponse login(@Validated @RequestBody JwtRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    public UserDto register(@Validated @RequestBody UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        User userCreated = userService.create(user);
        return userMapper.toDto(userCreated);
    }

    @PutMapping("/refresh")
    public JwtResponse refreshToken(@RequestBody String refreshToken) {
        return authService.refresh(refreshToken);
    }
}
