package com.rejs.reservation.global.security.jwt.token;

import lombok.Getter;

import java.util.Date;

@Getter
public class TokenWithExpire {
    private final String token;
    private final Long expiresAt;

    public TokenWithExpire(String token, Date expiresDate) {
        this.token = token;
        this.expiresAt = expiresDate.getTime();
    }
}
