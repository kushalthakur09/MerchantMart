package com.main.MerchantMart.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

@Component
public class JwtValidator extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt=request.getHeader(JwtConstants.JWT_AUTH_HEADER);
            if(jwt != null && jwt.startsWith(JwtConstants.JWT_AUTH_PREFIX)){
                jwt=jwt.substring(JwtConstants.JWT_AUTH_PREFIX.length());
                SecretKey key= Keys.hmacShaKeyFor(JwtConstants.JWT_SECERT_KEY.getBytes());

                Claims claims= Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
                String email= String.valueOf(claims.get("email"));
                String roles= String.valueOf(claims.get("authorities"));
                List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(roles);

                Authentication authentication=new UsernamePasswordAuthenticationToken(email,null,authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":401,\"message\":\"Invalid JWT Token\"}");
            return;
        }
        filterChain.doFilter(request,response);
    }
}
