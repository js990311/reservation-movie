package com.rejs.reservation.global.security.jwt.utils;

import com.rejs.reservation.global.security.jwt.token.ClaimsDto;
import com.rejs.reservation.global.security.jwt.token.Tokens;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtils {
    public static final String KEY_ROLE = "role";
    public static final String KEY_TYPE = "type";
    public static final String TYPE_ACCESS = "ACCESS";
    public static final String TYPE_REFRESH = "REFRESH";


    private final Key key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final JwtParser parser;

    public JwtUtils(String secretKey, long accessTokenExpiration, long refreshTokenExpiration) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.parser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // 토큰 생성

    public Tokens generateToken(String username, String role){
        Date now = new Date();
        return new Tokens(
                generateAccessToken(username, role, now),
                generateRefreshToken(username, now)
        );
    }

    private String generateAccessToken(String username, String role, Date issuedAt){
        Date expiryDate = new Date(issuedAt.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .setSubject(username)
                .claim(KEY_ROLE, role)
                .claim(KEY_TYPE, TYPE_ACCESS)
                .setIssuedAt(issuedAt)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    private String generateRefreshToken(String username, Date issuedAt){
        Date expiryDate = new Date(issuedAt.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .setSubject(username)
                .claim(KEY_TYPE, TYPE_REFRESH)
                .setIssuedAt(issuedAt)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    // 토큰 검증

    public boolean validateAccessToken(String token){
        return validateToken(token, TYPE_ACCESS);
    }

    public boolean validateRefreshToken(String token){
        return validateToken(token, TYPE_REFRESH);
    }

    private boolean validateToken(String token, String type){
        try {
            Jws<Claims> claimsJws = parser.parseClaimsJws(token);
            return claimsJws.getBody().get(KEY_TYPE, String.class).equals(type);
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

    // Claims
    public ClaimsDto getClaims(String token){
        return new ClaimsDto(parser.parseClaimsJws(token).getBody());
    }
}
