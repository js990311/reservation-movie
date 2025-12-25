package com.rejs.reservation.global.trace.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class ServletLoggingFilter extends OncePerRequestFilter{
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String method = request.getMethod();
        String requestURI = request.getRequestURI();

        log.info("[HTTP REQUEST] {} {}", method, requestURI);
        try {
            filterChain.doFilter(request,response);
        }finally {
            int status = response.getStatus();
            log.info("[HTTP RESPONSE] {} {} {}", method, requestURI, status);
        }
    }

}
