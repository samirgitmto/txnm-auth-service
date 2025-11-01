package com.txnm.auth.config.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtilOld {

	@Value("${jwt.secret}")
	private String jwtSecret;
	
	@Value("${jwt.expiration}")
	private long jwtExpirationMs;
	
	private Key getSigningKey() {
		byte[] decodedKeyBytes = Decoders.BASE64.decode(jwtSecret);
		
		// Creates a new SecretKey instance for use with HMAC-SHA algorithms based on the specified key byte array.
		return Keys.hmacShaKeyFor(decodedKeyBytes);
	}
	
	public String generateToken(UserDetails userDetails) {
		return generateTokenFromUsername(userDetails.getUsername());
	}

	private String generateTokenFromUsername(String username) {
		
		String jwt = Jwts.builder()
			.setSubject(username)
			.setIssuedAt(new Date())
			.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
			.signWith(getSigningKey(), SignatureAlgorithm.HS256)
			.compact();
		
		return jwt;
	}
	
	public String getUsernameFromToken(String token) {
		// a new JwtParser instance that can be configured create an immutable/thread-safe JwtParser.
		JwtParser jwtParser = Jwts.parserBuilder()
			.setSigningKey(getSigningKey())
			.build();
		
		String subject = jwtParser
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
		
		return subject;
	}

	public boolean validateToken(String token) {
		
		if (token == null || token.trim().isEmpty()) {
			return false;
		}
		
		try {
			JwtParser jwtParser = Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build();
			Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
			System.out.println("*************clamis");
			System.out.println(claimsJws.getBody());
			return true;
		}
		catch (JwtException | IllegalArgumentException e) {
			System.err.println(e.getMessage());
			return false;
		}
	}
}