package com.rejs.reservation.global.security.jwt.token;

import com.rejs.reservation.domain.user.entity.UserRole;
import com.rejs.reservation.global.security.jwt.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@Getter
public class ClaimsDto {
    private final String username;
    private final List<String> roles;
    private final String type;

    public ClaimsDto(Claims claims) {
        this.username = claims.getSubject();
        List<?> rawRoles = claims.get(JwtUtils.KEY_ROLE, List.class);
        if(rawRoles != null){
            this.roles = rawRoles.stream().map(String::valueOf).toList();
        }else {
            this.roles = new ArrayList<>();
        }
        this.type = claims.get(JwtUtils.KEY_TYPE, String.class);
    }
}
