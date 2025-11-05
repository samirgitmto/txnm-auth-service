package com.txnm.auth.config.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenValidator {

	private final JwtParser jwtParser;
//	@Value("${jwt.secret}")
    private String jwtSecret;
	
	public TokenValidator(@Value("${jwt.secret}") String jwtSecret2) {
		this.jwtSecret = jwtSecret2;
		System.err.println("jwt secret: " + jwtSecret);
		this.jwtParser = Jwts.parserBuilder()
							.setSigningKey(getSigningKey())
							.build();
	}
	
	private Key getSigningKey() {
        byte[] decodedKeyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(decodedKeyBytes);
    }
	
	public boolean isValidTokenWithoutBlacklistCheck(String token) {
		try {
			jwtParser.parseClaimsJws(token);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public Date getExpirationDateFromToken(String token) {
		return jwtParser.parseClaimsJws(token).getBody().getExpiration();
	}
}
