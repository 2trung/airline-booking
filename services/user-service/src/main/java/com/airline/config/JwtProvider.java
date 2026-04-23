package com.airline.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.duration}")
    private Long DURATION;
    
    private final SecretKey secretKey = Keys.hmacShaKeyFor(
            SECRET_KEY.getBytes()
    );

    public String generateToken(Authentication authentication, Long userId) {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roles = populateAuthorities(authorities);
        String jwt = Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + DURATION))
                .claim("email", authentication.getName())
                .claim("authorities", roles)
                .claim("userId", userId)
                .signWith(secretKey)
                .compact();
        return jwt;
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> auths = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            auths.add(authority.getAuthority());
        }
        return String.join(",", auths);
    }
}
