package com.rejs.reservation.global.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.domain.user.entity.UserRole;
import com.rejs.reservation.global.dto.response.BusinessExceptionResponse;
import com.rejs.reservation.global.security.exception.AuthenticationExceptionCode;
import com.rejs.reservation.global.security.filter.JwtAuthenticationFilter;
import com.rejs.reservation.global.security.jwt.properties.JwtProperties;
import com.rejs.reservation.global.security.jwt.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.PrintWriter;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties({JwtProperties.class})
public class SecurityConfig {
    private final ObjectMapper mapper;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtUtils jwtUtils(JwtProperties properties) {
        if (!StringUtils.hasText(properties.getSecretKey())) {
            throw new IllegalArgumentException("Require jwt.secret-key in Properties");
        } else {
            return new JwtUtils(properties.getSecretKey(), properties.getAccessTokenExpiration(), properties.getRefreshTokenExpiration());
        }
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtils jwtUtils) {
        return new JwtAuthenticationFilter(jwtUtils);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf->csrf.disable())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth
                        .requestMatchers(HttpMethod.POST, "/movies", "/screenings", "/theaters").hasRole(UserRole.ROLE_ADMIN.getRoleName())
                        .requestMatchers("/login", "/signup").permitAll()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/docs/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/movies/**", "/theaters/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().hasAnyRole(UserRole.ROLE_USER.getRoleName(), UserRole.ROLE_ADMIN.getRoleName())
                )
                .cors(cors->cors.configurationSource(corsConfigurationSource()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(((request, response, ex) -> {
                            response.setContentType("application/json");
                            AuthenticationExceptionCode code = AuthenticationExceptionCode.INVALID_TOKEN;
                            response.setStatus(code.getStatus().value());
                            PrintWriter writer = response.getWriter();
                            writer.write(mapper.writeValueAsString(new BusinessExceptionResponse(code, request.getRequestURI(), null)));
                        }))
                        .accessDeniedHandler((request, response, ex)->{
                                    response.setContentType("application/json");
                                    AuthenticationExceptionCode code = AuthenticationExceptionCode.INVALID_TOKEN;
                                    response.setStatus(code.getStatus().value());
                                    PrintWriter writer = response.getWriter();
                                    writer.write(mapper.writeValueAsString(new BusinessExceptionResponse(code, request.getRequestURI(), null)));
                                }
                        ))
        ;
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST"));

        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
