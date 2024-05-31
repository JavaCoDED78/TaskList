package com.javaded.web.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.util.Optional;

@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @SneakyThrows
    public void doFilter(
            final ServletRequest servletRequest,
            final ServletResponse servletResponse,
            final FilterChain filterChain
    ) {
        Optional<String> maybeBearerToken = jwtTokenProvider
                .resolveToken((HttpServletRequest) servletRequest);
        try {
            maybeBearerToken.ifPresent(this::onValidToken);
        } catch (
                Exception ignored
        ) {
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void onValidToken(
            final String token
    ) {
        if (jwtTokenProvider.isValid(token)) {
            Authentication authentication = jwtTokenProvider
                    .getAuthentication(token);
            authenticate(authentication);
        }
    }

    private void authenticate(
            final Authentication authentication
    ) {
        if (authentication != null) {
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }
    }
}
