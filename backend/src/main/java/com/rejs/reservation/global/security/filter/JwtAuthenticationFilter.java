package com.rejs.reservation.global.security.filter;

import com.rejs.reservation.global.security.jwt.token.ClaimsDto;
import com.rejs.reservation.global.security.jwt.utils.JwtUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
            if(authorization != null && authorization.startsWith("Bearer ")) {
                String token = authorization.substring(7);
                boolean isAccessToken = jwtUtils.validateAccessToken(token);
                if (isAccessToken) {
                    Authentication authentication = createAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(createAuthentication(token));
                    ClaimsDto claims = (ClaimsDto) authentication.getPrincipal();
                    if (claims != null && claims.getUsername() != null) {
                        MDC.put("userId", claims.getUsername());
                    }
                    log.debug("[jwt.success] 인증 성공");
                }else {
                    log.debug("[jwt.fail.invalid] 인증 실패");
                }
            }else {
                log.debug("[jwt.empty] 토큰 부재");
            }
        }catch (JwtException ex){
            log.debug("[jwt.fail.exception] 예외로 인한 인증 실패");
            throw new BadCredentialsException("Invalid or Expired Token", ex);
        }

        filterChain.doFilter(request,response);
        SecurityContextHolder.clearContext();
        MDC.put("userId", ""); // 안전을 위해서 소각
    }

    protected Authentication createAuthentication(String token){
        ClaimsDto claims = jwtUtils.getClaims(token);
        return new UsernamePasswordAuthenticationToken(
                claims,
                null,
                claims.getRoles().stream().map(SimpleGrantedAuthority::new).toList()
        );
    }


}
