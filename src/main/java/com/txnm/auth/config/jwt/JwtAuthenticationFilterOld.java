package com.txnm.auth.config.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilterOld extends OncePerRequestFilter {

	@Autowired
	private JwtUtilOld jwtUtil;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			// 1. Extract JWT from Authorization header
			String token = extractJwtFromRequest(request);
			if (token == null) return;
			
	        // 2. Validate token & signature
			if (jwtUtil.validateToken(token)) {
				// 3. Load UserDetails from claims
				UserDetails userDetails = extractUserDetailsFromToken(token);
				
		        // 4. Set SecurityContext with authentication
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
			
			filterChain.doFilter(request, response);
		}
		catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
	
	private String extractJwtFromRequest(HttpServletRequest request) {
	    String bearerToken = request.getHeader("Authorization");
	    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
	        return bearerToken.substring(7);
	    }
	    return null;
	}

	private boolean validateToken(String jwt) {
	    // Validate token expiration, signature, etc.
	    // You'll need JWT utility class for this
	    return jwtUtil.validateToken(jwt);
	}

	private UserDetails extractUserDetailsFromToken(String jwt) {
	    // Extract username/email from token claims
		String usernameFromToken = jwtUtil.getUsernameFromToken(jwt);
		
	    // Load UserDetails from database or create from claims
	    return null; // placeholder
	}
}