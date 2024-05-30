package com.javaded.web.security;

import com.javaded.domain.exception.AccessDeniedException;
import com.javaded.domain.user.Role;
import com.javaded.domain.user.User;
import com.javaded.service.UserService;
import com.javaded.service.props.JwtProperties;
import com.javaded.web.dto.auth.JwtResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys
                .hmacShaKeyFor(jwtProperties
                        .getSecret().getBytes()
                );
    }

    public String createAccessToken(final Long userId,
                                    final String username,
                                    final Set<Role> roles
    ) {
        Claims claims = Jwts.claims()
                .subject(username)
                .add("id", userId)
                .add("roles", resolveRoles(roles))
                .build();
        Instant validity = Instant.now()
                .plus(jwtProperties.getAccess(),
                        ChronoUnit.HOURS
                );
        return compactClams(claims, validity);
    }

    public String createRefreshToken(final Long userId,
                                     final String username
    ) {
        Claims claims = Jwts.claims()
                .subject(username)
                .add("id", userId)
                .build();
        Instant validity = Instant.now()
                .plus(jwtProperties.getRefresh(), ChronoUnit.DAYS);
        return compactClams(claims, validity);
    }

    public JwtResponse refreshUserTokens(final String refreshToken) {
        if (!isValid(refreshToken)) {
            throw new AccessDeniedException();
        }
        Long userId = Long.valueOf(extractIdFromToken(refreshToken));
        User user = userService.getById(userId);
        return JwtResponse.builder()
                .id(userId)
                .username(user.getUsername())
                .accessToken(createAccessToken(
                        userId, user.getUsername(),
                        user.getRoles())
                )
                .refreshToken(createRefreshToken(
                        userId,
                        user.getUsername())
                )
                .build();
    }

    private String extractIdFromToken(final String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id", String.class);
    }

    public boolean isValid(final String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .after(new Date());
    }

    public Authentication getAuthentication(final String token) {
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(getUsernameFromToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails,
                "",
                userDetails.getAuthorities()
        );
    }

    private String getUsernameFromToken(final String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Optional<String> resolveToken(
            final HttpServletRequest request
    ) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return Optional.of(bearerToken.substring(7));
        }
        return Optional.ofNullable(bearerToken);
    }

    private String compactClams(final Claims claims,
                                final Instant validity
    ) {
        return Jwts.builder()
                .claims(claims)
                .expiration(Date.from(validity))
                .signWith(key)
                .compact();
    }

    private List<String> resolveRoles(final Set<Role> roles) {
        return roles.stream()
                .map(Enum::name)
                .toList();
    }
}
