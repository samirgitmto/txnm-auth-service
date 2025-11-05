package com.txnm.auth.config.jwt;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.txnm.auth.dao.AuthnProvider;
import com.txnm.auth.dao.TxnmUser;
import com.txnm.auth.service.TokenBlacklist;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;
    
    @Autowired
    @Qualifier("TokenBlacklistImplConcurrentSet")
    private TokenBlacklist tokenBlacklist;
    
    private Key getSigningKey() {
        byte[] decodedKeyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(decodedKeyBytes);
    }
    
    public String generateToken(UserDetails userDetails) {
        return generateTokenFromUsername(userDetails.getUsername());
    }

    public String generateTokenFromUsername(String email) {
        return Jwts.builder()
            .setSubject(email) // Using email as subject
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    public String getEmailFromToken(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build();
        
        String email = jwtParser
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
        
        return email;
    }

    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        if (tokenBlacklist.isBlacklisted(token)) {
        	return false;
        }
        
        try {
            JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build();
            Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
            System.out.println("Header: " + claimsJws.getHeader());
            System.out.println("Body: " + claimsJws.getBody());
            System.err.println(claimsJws.getSignature());
            return true;
        }
        catch (ExpiredJwtException e) {
            System.err.println("JWT token expired: " + e.getMessage());
            return false;
        }
        catch (MalformedJwtException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
            return false;
        }
        catch (SecurityException e) {
            System.err.println("Invalid JWT signature: " + e.getMessage());
            return false;
        }
        catch (JwtException | IllegalArgumentException e) {
            System.err.println("JWT validation error: " + e.getMessage());
            return false;
        }
    }
    
    // Optional: Add method to get expiration date
    public Date getExpirationDateFromToken(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build();
        
        return jwtParser.parseClaimsJws(token).getBody().getExpiration();
    }

    public void blacklistToken(String token) {
        tokenBlacklist.addToBlacklist(token);
    }

}