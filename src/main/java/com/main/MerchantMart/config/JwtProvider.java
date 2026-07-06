package com.main.MerchantMart.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class JwtProvider {

    private static final SecretKey JWT_KEY = Keys.hmacShaKeyFor(JwtConstants.JWT_SECERT_KEY.getBytes());
    private static final long JWT_EXPIRATION =24L * 60 * 60 * 1000;


    public String generateToken(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roles = populateAuthorities(authorities);

        return Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime()+JWT_EXPIRATION))
                .claim("email",authentication.getName())
                .claim("authorities",roles)
                .signWith(JWT_KEY)
                .compact();
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> roles=new HashSet<>();

        for(GrantedAuthority authority:authorities){
            roles.add(String.valueOf(authority));
        }
        return String.join(",",roles);
    }

    public String getEmailFromToken(String jwt){
        if (jwt !=null && jwt.startsWith(JwtConstants.JWT_AUTH_PREFIX)) {
            jwt = jwt.substring(JwtConstants.JWT_AUTH_PREFIX.length());
        }
        return Jwts.parser().verifyWith(JWT_KEY).build().parseSignedClaims(jwt).getPayload().get("email").toString();
    }
}
