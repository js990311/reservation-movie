package com.rejs.reservation.global.security.filter;

import com.rejs.reservation.global.security.jwt.token.ClaimsDto;
import com.rejs.reservation.global.security.jwt.utils.JwtUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorization = request.getHeader("Authorization");
            if(authorization != null && authorization.startsWith("Bearer ")){
                String token = authorization.substring(7);
                boolean isAccessToken = jwtUtils.validateAccessToken(token);
                if(isAccessToken){
                    log.info("[filter] 토큰이 인증되었음");
                    SecurityContextHolder.getContext().setAuthentication(createAuthentication(token));
                }else {
                    log.info("[filter] 토큰이 존재하지만 인증되지 못했음");
                }
            }else {
                log.info("[filter] 토큰이 존재하지 않음");
            }
        }catch (JwtException ex){
            throw new BadCredentialsException("Invalid or Expired Token", ex);
        }
        filterChain.doFilter(request,response);
        SecurityContextHolder.clearContext();
    }

    protected Authentication createAuthentication(String token){
        ClaimsDto claims = jwtUtils.getClaims(token);
        return new UsernamePasswordAuthenticationToken(
                claims.getUsername(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority(claims.getRole()))
        );
    }


}
