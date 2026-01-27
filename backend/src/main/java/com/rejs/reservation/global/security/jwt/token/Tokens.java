package com.rejs.reservation.global.security.jwt.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Tokens {
    private final TokenWithExpire accessToken;
    private final TokenWithExpire refreshToken;
}
