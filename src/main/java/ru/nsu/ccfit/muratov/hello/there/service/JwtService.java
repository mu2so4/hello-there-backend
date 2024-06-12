package ru.nsu.ccfit.muratov.hello.there.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.nsu.ccfit.muratov.hello.there.dto.auth.LoginDto;

import java.security.Key;
import java.util.Date;

@Component
public class JwtService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.signing.key}")
    private String signingKey;

    public String generateToken(Authentication authentication) {
        String user = authentication.getName();
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + expiration);
        return Jwts.builder()
                .subject(user)
                .issuedAt(new Date())
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }
        catch(Exception e) {
            //throw new AuthenticationCredentialsNotFoundException("JWT expired or invalid", e);
            return false;
        }
    }

    public String login(LoginDto dto) throws AuthenticationException {
        String username = dto.getUsername();
        Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return generateToken(authentication);
    }


    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(signingKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
