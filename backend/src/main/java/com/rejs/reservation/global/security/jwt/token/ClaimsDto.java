package com.rejs.reservation.global.security.jwt.token;

import com.rejs.reservation.global.security.jwt.utils.JwtUtils;
import io.jsonwebtoken.Claims;

public class ClaimsDto {
    private final String username;
    private final String role;
    private final String type;

    public ClaimsDto(Claims claims) {
        this.username = claims.getSubject();
        this.role = claims.get(JwtUtils.KEY_ROLE, String.class);
        this.type = claims.get(JwtUtils.KEY_TYPE, String.class);
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getType() {
        return type;
    }
}
