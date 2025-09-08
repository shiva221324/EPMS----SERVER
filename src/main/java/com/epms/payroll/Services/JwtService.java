package com.epms.payroll.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import javax.crypto.SecretKey;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private  String secretKey;
    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    public String extractUsername(String jwtToken) {

        return extractClaim(jwtToken, Claims::getSubject);
    }

    private <T> T extractClaim(String jwtToken, Function<Claims,T> claimResolver) {
        final Claims claims=extractAllClaims(jwtToken);

        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwtToken) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    private String generateToken(HashMap<String,Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+jwtExpiration))
                .signWith(getSignInKey())
                .compact();
    }
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(),userDetails);
    }
    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public boolean isTokenValid(String jwtToken, UserDetails userdetails) {
        final String username = extractUsername(jwtToken);
        return (userdetails.getUsername().equals(username) && !isTokenExpired(jwtToken));
    }


    private boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }
}
